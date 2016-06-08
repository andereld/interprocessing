(ns interprocessing.bits)

(defn rand-bits
  "Returns a random long between 0 (inclusive) and 2^n (exclusive),
  i.e. a random n-bit number, with n ∈ [1, 64)."
  [n]
  (long (rand (bit-shift-left 1 n))))

(defn normalize-16-bits
  "Takes a 16-bit integer value and returns a floating-point value
  in [0, 1] (inclusive)."
  [n]
  (let [max-value (double (dec (bit-shift-left 1 16)))]
    (/ n max-value)))

(defn rand-long
  "Returns a random (64-bit) java.lang.Long. Because JVM longs are signed,
  this is different from simply calling (long (rand Long/MAX_VALUE)),
  which would only give you 63 random bits."
  []
  (let [higher-bits (rand-bits 63)
        lowest-bit (rand-int 2)]
    (bit-or (bit-shift-left higher-bits 1) lowest-bit)))

(defn test-64-bits [n]
  (let [values (for [i (range 64)]
                 (bit-test n i))
        set-bits (count (filter true? values))
        clear-bits (- 64 set-bits)]
    {:clear-bits clear-bits :set-bits set-bits}))

(defn flip-random-bit
  "Flips one randomly chosen bit in a long. If a size n is given, the long
  is treated as being n bits in size; i.e., only the rightmost n bits may be
  affected."
  [n & {:keys [size] :or {size 64}}]
  (bit-flip n (rand-int size)))

(defn long->quadruple
  "Takes a 64-bit long, partitions it into four 16-bit integer values
  and returns a sequence of those values."
  [n]
  (let [number-of-parts 4
        bit-mask (dec (bit-shift-left 1 16))]
    (loop [n' n
           remaining number-of-parts
           parts []]
      (if (zero? remaining)
        parts
        (recur (bit-shift-right n' 16)
               (dec remaining)
               (conj parts (bit-and n' bit-mask)))))))

(defn to-binary-string
  "Converts a long to its binary representation as a java.lang.String.
  If min-length is given, the resulting string will be at least min-length
  characters long, padding the left-hand side with zeroes."
  [n & {:keys [min-length] :or {min-length 0}}]
  (let [string (Long/toBinaryString n)
        missing-chars (- min-length (count string))]
    (if (pos? missing-chars)
      (str (apply str (repeat missing-chars "0")) string)
      string)))

(defn hamming-distance [n1 n2]
    (let [string1 (to-binary-string n1 :min-length 64)
          string2 (to-binary-string n2 :min-length 64)]
      (count (filter true? (map not= string1 string2)))))
