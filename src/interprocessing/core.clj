(ns interprocessing.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.pprint :refer [pprint]]
            [genetic.core :as genetic]
            [interprocessing.csound-wrapper :as cs]
            [interprocessing.fitness :as fitness]
            [interprocessing.util :refer [read-csv-file]]))

(def process-script (.getPath (io/resource "csound/analyze_and_process.csd")))
(def analyze-script (.getPath (io/resource "csound/analyze_results.csd")))
(def result-file "audio_output/result.wav")

(defn process-and-analyze!
  "Run the Csound processing and analysis, causing existing
  audio and CSV output files to be overwritten."
  []
  (let [resource-dir (.getPath (io/resource "csound/"))]
    (shell/with-sh-dir resource-dir
      (shell/sh "csound" process-script)
      (shell/sh "csound" analyze-script))
    (let [affected (read-csv-file (io/resource "analysis_output/affected.csv"))
          affector (read-csv-file (io/resource "analysis_output/affector.csv"))
          result (read-csv-file (io/resource "analysis_output/result.csv"))]
      [affector affected result])))

(defn interconnect! [params mod-source chan scales]
  (let [interconnections (mapv #(vector %1 mod-source chan %2) params scales)]
    (cs/write-interconnections-to-include-file! interconnections)))

(defn setup! [affector affected effect-name mod-source]
  (if-let [effect (cs/get-instrument
                    effect-name
                    (io/file (io/resource "csound/includes/")))]
    (let [chan 1
          params (:params effect)
          random-scales (repeatedly (count params) rand)]
      (cs/write-instrument-includes-to-include-file! effect-name)
      (cs/write-affector-analysis-to-include-file! affector)
      (interconnect! params mod-source chan random-scales)
      (cs/write-instrument-score-events-to-include-file! effect-name)
      (cs/write-effect-score-events-to-include-file! effect affected)
      (cs/write-affected-analysis-to-include-file! affected)
      (cs/write-result-analysis-to-include-file! result-file)
      [effect chan])
    (throw (Exception. "Init failed: Effect instrument not found."))))

(defn new-individual [size]
  (comp vec (partial repeatedly size rand)))

(defn mutate [individual]
  (let [index (rand-int (count individual))
        original-value (nth individual index)
        add? (< (rand) 0.5)
        diff (rand (/ original-value 2.0))
        new-value (-> (if add?
                        (+ original-value diff)
                        (- original-value diff))
                      (max 0)
                      (min 1))]
  (assoc individual index new-value)))

(defn crossover [i1 i2]
  (let [crossover-point (rand-int (count i1))]
    (vec (concat (take crossover-point i1) (drop crossover-point i2)))))

(defn fitness [params mod-source chan]
  (memoize
    (fn [individual]
      (interconnect! params mod-source chan individual)
      (let [[affector affected result] (process-and-analyze!)
            affector-diff (fitness/rms-difference affector result)
            affected-diff (fitness/rms-difference affected result)]
        (/ 1.0 affector-diff)))))

(defn evolve-scales [params mod-source chan]
  (let [multiple-params? (> (count params) 1)
        mutate-weight (if multiple-params? 1/2 3/4)
        crossover-weight (if multiple-params? 1/4 0)
        clone-weight (- 1 mutate-weight crossover-weight)]
    (genetic/evolve :new-individual (new-individual (count params))
                    :mutate mutate
                    :crossover crossover
                    :crossover-weight crossover-weight
                    :clone-weight clone-weight
                    :fitness (fitness params mod-source chan)
                    :popsize 5
                    :max-generations 20
                    :acceptable-fitness (/ 1.0 15.0))))

(defn -main [& args]
  (let [affector "audio_input/StruglKor2mono.wav"
        affected "audio_input/WhiteNoise.wav"
        effect "amplitude_tracker"
        mod-source "rms"
        [effect chan] (setup! affector affected effect mod-source)]
    (evolve-scales (:params effect) mod-source chan)
    (shutdown-agents)))
