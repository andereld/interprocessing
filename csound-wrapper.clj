(ns interprocessing.csound-wrapper
  "Functions for reading data from existing Csound source files, and for
  creating new ones. There are a number of hardcoded constants as avoiding this
  would necessitate rewriting much of the existing code base, most likely in
  painful ways."
  (:require [clojure.string :as str]))

(def modulation-sources ["rms" "rms_preEq" "cps" "pitch" "centroid" "spread"
                         "skewness" "kurtosis" "flatness" "crest" "flux"
                         "amp_trans" "centr_trans" "kurt_trans" "pitchup_trans"
                         "pitchdown_trans" "cps_raw"])

(defn init-files [dir]
  "Returns all init files generated by codeUtility.py in the given directory."
  (let [files (file-seq (clojure.java.io/file dir))]
    (filter #(.endsWith (.getName %) "parameters_offline.inc") files)))

(defn parse-init-file [file]
  "Returns the instrument id and list of available parameters
  from a given init file."
  (let [contents (slurp file)
        instr-id (second (re-find #"instr (\d+)" contents))
        params (mapv second (re-seq #"(\w+)_min\"" contents))]
    {:instr-id instr-id
     :params params}))

(defn get-instruments [dir]
  "Returns a list of instruments located in the given directory.
  Each instrument is a map of its name, id, associated filenames
  and mappable parameters."
  (mapv (fn [init-file]
          (let [init-filename (.getName init-file)
                instr-name (second (re-find #"(\w+)_parameters_offline.inc"
                                            init-filename))
                instr-filename (str instr-name "_offline.inc")
                attributes (parse-init-file init-file)]
            (into {:name instr-name
                   :init-file init-filename
                   :instr-file instr-filename}
                  attributes)))
        (init-files dir)))

;;; In the following helper functions, instrument numbers, start and duration
;;; times as well as channel prefixes are hardcoded to match the contents of
;;; analyze_and_process.csd.

(defn source-str [param mod-source]
  (str "i 21\t3.5\t0.1\t\"source1_" param "\"\t\"" mod-source \"))

(defn chan-str [param chan]
  (str "i 22\t3.5\t0.1\t\"chan1_" param "\"\t" chan))

(defn scale-str [param scale]
  (str "i 22\t3.5\t0.1\t\"scale1_" param "\"\t" scale))

(defn interconnect [param mod-source chan scale]
  "Takes an instrument parameter, a modulation source, a communications channel
  and the weight (scale) of the modulation source upon the effect instrument.
  Returns the necessary Csound code to make the given connection as a string."
  (let [source (source-str param mod-source)
        chan (chan-str param chan)
        scale (scale-str param scale)]
    (str/join "\n" [source chan scale])))

;(defn write

(defn include-str [filename]
  (str "#include \"" filename \"))

(defn include-instruments [instrument-names]
  "Takes a list of instruments and returns the necessary Csound code to include
  its initialization and instrument files relative to the current directory."
  (let [include-strs (for [instr-name instrument-names
                           :let [init (str instr-name "_parameters_offline.inc")
                                 instr (str instr-name "_offline.inc")]]
                       (str/join "\n" [(include-str init) (include-str instr)]))]
    (str/join "\n" include-strs)))
