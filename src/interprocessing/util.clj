(ns interprocessing.util
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn relative-diff
  "Takes two numbers and returns their relative difference."
  [n1 n2]
  (/ (Math/abs (- n1 n2))
     (/ (+ (Math/abs n1) (Math/abs n2)) 2)))

(defn normalize-to-unity
  "Takes a vector of numbers, divides each number by the total sum of the
  numbers such that the resulting vector sums to one and returns this vector."
  [v]
  (let [sum (apply + v)
        scale (fn [n] (double (/ n sum)))]
    (mapv scale v)))

(defn iso8601-time
  "Returns the current date and time as a string in ISO8601 format."
  []
  (let [now (java.util.Date.)
        fmt (java.text.SimpleDateFormat. "yyyy-MM-dd'T'HHmmss")]
    (.format fmt now)))

(defn parse-relative-path
  "Takes a relative path and returns it as a canonical (absolute) path."
  [relative-path]
  (if (or (nil? relative-path) (str/blank? relative-path))
    (throw (Exception. "Path cannot be empty."))
    (.getCanonicalPath (io/file relative-path))))

(defn parse-list-of-numbers
  "Takes a string of comma-separated numbers
  and returns them as a vector of doubles."
  [numbers]
  (mapv #(Double/parseDouble %) (str/split numbers #",")))

(defn map-seq->csv
  "Converts a sequence of maps to CSV data."
  [map-seq]
  (let [columns (keys (first map-seq))
        column-names (map name columns)]
    (loop [csv (str (str/join "," column-names) \newline)
           data map-seq]
      (if (empty? data)
        csv
        (recur
          (str csv (str/join "," (map #(% (first data)) columns)) \newline)
          (rest data))))))
