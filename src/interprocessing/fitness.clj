(ns interprocessing.fitness
  (:require [interprocessing.util :refer :all]))

(defn difference [pairs]
  (reduce (fn [acc pair]
            (+ acc (Math/abs (apply - pair))))
          0 pairs))

(defn rms-difference [control result]
  (let [rms-values (map vector (:rms1 control) (:rms1 result))]
    (difference rms-values)))

(defn parameter-differences [control-file result-file]
  (let [control (read-csv-file control-file)
        result (read-csv-file result-file)
        params (into {} (for [k (keys control)
                              :let [values (map vector (k control) (k result))]]
                          {k values}))]
    (reduce (fn [acc [k vs]]
              (into acc {k (difference vs)}))
            {} params)))
