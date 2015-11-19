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

(defn drop-leading-silence [table]
  (let [silent-rows (count (take-while zero? (:rms1 table)))]
    (into table (for [k (keys table)]
                  {k (drop silent-rows (k table))}))))

(defn rms-difference [control-file result-file]
  (let [control (drop-leading-silence (read-csv-file control-file))
        result (drop-leading-silence (read-csv-file result-file))
        rms-values (map vector (:rms1 control) (:rms1 result))]
    (reduce (fn [acc pair]
              (+ acc (Math/abs (apply - pair))))
            0 rms-values)))
