(ns interprocessing.core
  (:gen-class)
  (:require [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]
            [interprocessing.runner :as runner]
            [interprocessing.csound :as csound]
            [interprocessing.instruments :as inst]
            [interprocessing.util :refer [parse-relative-path
                                          parse-list-of-numbers]]))

; Reflection can be slow, so we want the compiler to inform us when
; it's unable to resolve a Java method call or field access without it.
(set! *warn-on-reflection* true)

(def cli-options
  [["-i"
    "--affected AFFECTED_AUDIO_FILE"
    "Input audio file to be affected by FX processing."
    :parse-fn parse-relative-path]
   ["-a"
    "--affector AFFECTING_AUDIO_FILE"
    "Audio file whose analyzed features will affect the FX processing."
    :parse-fn parse-relative-path]
   ["-d"
    "--debug"
    "Print debug information while running."]
   ["-e"
    "--effect EFFECT_NAME"
    (str "The effect to be applied. One of: "
         (str/join ", " (keys inst/effects)))
    :validate [#(contains? inst/effects %)
               "Please specify a valid effect."]]
   ["-f"
    "--frame-size FRAME_SIZE"
    "The duration of each frame to be analyzed processed. If no frame size
    is given, the entire input file is processed in one pass using static
    effect parameters."
    :parse-fn #(Double/parseDouble %)]
   ["-h"
    "--help"
    "Print this help message."]
   ["-m"
    "--max-iterations MAX_ITERATIONS"
    "Maximum number of iterations without change for a run of the genetic
    algorithm."
    :default 100
    :parse-fn #(Integer/parseUnsignedInt %)]
   ["-o"
    "--output-dir OUTPUT_DIRECTORY"
    "The directory in which output audio and analysis files should be placed."
    :default "output"
    :parse-fn parse-relative-path]
   ["-w"
    "--weights FEATURE_WEIGHTS"
    "Three weights separated by commas representing the importance of the mean
    centroid, pitch and RMS amplitude values in the evaluation of fitness for
    a set of FX parameters. The values must appear in-order, must not contain
    negative numbers and must contain at least one value greater than zero."
    :default [0.0 0.0 1.0]
    :parse-fn parse-list-of-numbers]])

(defn -main [& args]
  ; Handle CLI arguments.
  (let [{:keys [options errors summary]} (parse-opts args cli-options)]
    (when (:help options)
      (println summary)
      (System/exit 0))
    (when (not-every? #(contains? options %) [:affected :affector :effect])
      (println (str "You must specify both an affected and an affecting file, "
                    "as well as an effect." \newline
                    "Pass the --help flag for more information."))
      (System/exit 1))
    (when errors
      (println (str/join \newline errors))
      (System/exit 1)))

  ; Run genetic algorithm, performing audio processing and analysis for each
  ; solution being considered.
  (let [{:keys [options]} (parse-opts args cli-options)
        {:keys [affector affected effect output-dir
                weights frame-size max-iterations debug]} options
        csound-instance (csound/init-csound)
        instrument ((get inst/effects effect))]
    (let [start-time (System/currentTimeMillis)
          run-data (if frame-size
                     (runner/solve-dynamic! csound-instance affector affected
                                            instrument weights output-dir
                                            frame-size
                                            :max-iterations max-iterations
                                            :debug? debug)
                     (runner/solve! csound-instance affector affected
                                    instrument weights output-dir
                                    :max-iterations max-iterations
                                    :debug? debug))]
      (runner/log! run-data) ; write analysis data to output directory
      (csound/stop-csound csound-instance) ; shut down Csound
      (println (str "Completed run successfully in "
                    (double (/ (- (System/currentTimeMillis) start-time) 1000))
                    " seconds.")))))
