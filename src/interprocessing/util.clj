(ns interprocessing.util)

(defn transpose [matrix]
  (apply mapv vector matrix))

(defn strings->doubles [lst]
  (map #(Double/parseDouble %) lst))

(defn read-csv-file [filename]
  (let [contents (slurp filename)
        lines (clojure.string/split-lines contents)
        rows (map #(clojure.string/split % #",") lines)
        column-headers (map keyword (first rows))
        data (map strings->doubles (transpose (rest rows)))]
    (zipmap column-headers data)))

(defn find-by-key-value-pair
  "Returns the first map in a sequence of maps which contains the given
  key-value pair. Returns nil if no such map is found."
  [ms k v]
  (first (filter #(= (k %) v) ms)))
