(ns interprocessing.util)

(defn find-by-key-value-pair
  "Returns the first map in a sequence of maps which contains the given
  key-value pair. Returns nil if no such map is found."
  [ms k v]
  (first (filter #(= (k %) v) ms)))
