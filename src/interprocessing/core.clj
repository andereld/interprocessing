(ns interprocessing.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.pprint :refer [pprint]]
            [interprocessing.csound-wrapper :as cs]
            [interprocessing.fitness :as fitness]))

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
      (shell/sh "csound" analyze-script))))

(defn setup! [affector affected effect-name mod-source]
  (if-let [effect (cs/get-instrument
                    effect-name
                    (io/file (io/resource "csound/includes/")))]
    (let [chan 1
          interconnections (mapv
                             #(vector % mod-source chan (rand 1))
                             (:params effect))]
      (cs/write-instrument-includes-to-include-file! effect-name)
      (cs/write-affector-analysis-to-include-file! affector)
      (cs/write-interconnections-to-include-file! interconnections)
      (cs/write-instrument-score-events-to-include-file! effect-name)
      (cs/write-effect-score-events-to-include-file! effect affected)
      (cs/write-affected-analysis-to-include-file! affected)
      (cs/write-result-analysis-to-include-file! result-file))
    (throw (Exception. "Init failed: Effect instrument not found."))))

(defn -main [& args]
  (let [affector "audio_input/StruglKor2mono.wav"
        affected "audio_input/WhiteNoise.wav"
        effect "amplitude_tracker"
        mod-source "rms"]
    (setup! affector affected effect mod-source) 
    (process-and-analyze!)
    (shutdown-agents) ; terminate the thread pool used by shell
    (let [affected (io/resource "analysis_output/affected.csv")
          affector (io/resource "analysis_output/affector.csv")
          result (io/resource "analysis_output/result.csv")]
      (pprint (fitness/parameter-differences affector result)))))
