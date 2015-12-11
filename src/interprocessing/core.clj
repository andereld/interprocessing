(ns interprocessing.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [interprocessing.csound-wrapper :as csound]
            [interprocessing.fitness :as fitness]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [source (io/resource "analysis_output/source.csv")
        control (io/resource "analysis_output/control.csv")
        result (io/resource "analysis_output/result.csv")]
    (pprint (fitness/parameter-differences control result))))
