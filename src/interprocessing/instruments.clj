(ns interprocessing.instruments
  (:require [interprocessing.bits :refer :all]
            [interprocessing.csound :as csound]))

(defprotocol Instrument
  (genotype->fx-parameters [this genotype])
  (set-fx-parameters [this csound-instance genotype])
  (mutate-genotype [this genotype])
  (phenotype-fn
    [this csound-instance affected-file output-file]
    [this csound-instance affected-file output-file skip-time duration])
  (process
    [this csound-instance genotype affected output]
    [this csound-instance genotype affected output skip-time duration]
    [this csound-instance genotype affected output skip-time duration
     callback interval]))

(defrecord LPDistortion []
  Instrument
  (genotype->fx-parameters [this genotype]
    (let [[v1 v2 v3 v4] (long->quadruple genotype)
          coefficient-a (normalize-16-bits v1)
          coefficient-b (normalize-16-bits v2)
          cutoff (+ (* (normalize-16-bits v3) 9950) 50)  ; arbitrary, but sound
          resonance (normalize-16-bits v4)]
      {:coefficient-a coefficient-a
       :coefficient-b coefficient-b
       :cutoff cutoff
       :resonance resonance}))

  (set-fx-parameters [this csound-instance genotype]
    (let [params (genotype->fx-parameters this genotype)
          a (:coefficient-a params)
          b (:coefficient-b params)
          cutoff (:cutoff params)
          resonance (:resonance params)]
      (csound/set-channel-value csound-instance "coefficient-a" a)
      (csound/set-channel-value csound-instance "coefficient-b" b)
      (csound/set-channel-value csound-instance "cutoff" cutoff)
      (csound/set-channel-value csound-instance "resonance" resonance)))

  (mutate-genotype [this genotype]
    (let [mutations (+ 1 (rand-int 4))
          delta (/ 1 mutations)]
      (loop [remaining mutations
             p 1
             new-genotype genotype]
        (if (zero? remaining)
          new-genotype
          (if (< (rand) p)
            (recur (dec remaining) (- p delta) (flip-random-bit new-genotype))
            (recur (dec remaining) (- p delta) new-genotype))))))

  (phenotype-fn [this csound-instance affected-file output-file]
    (phenotype-fn this csound-instance affected-file output-file 0 0xDEADBEEF))

  (phenotype-fn
    [this csound-instance affected-file output-file skip-time duration]
    (memoize
      (fn [genotype]
        (process this csound-instance genotype affected-file output-file
                 skip-time duration)
        (csound/analyze csound-instance output-file))))

  (process [this csound-instance genotype affected output]
    (process this csound-instance genotype affected output 0 0xDEADBEEF))

  (process [this csound-instance genotype affected output skip-time duration]
    (set-fx-parameters this csound-instance genotype)
    (csound/process csound-instance 2 affected output
                    :skip-time skip-time :duration duration))

  (process [this csound-instance genotype affected output skip-time duration
            callback interval]
    (set-fx-parameters this csound-instance genotype)
    (csound/process csound-instance 2 affected output
                    :skip-time skip-time :duration duration
                    :callback callback :interval interval)))

(defrecord Gain []
  Instrument
  (genotype->fx-parameters [this genotype]
    (let [bit-mask (dec (bit-shift-left 1 16))
          lower-bits (bit-and genotype bit-mask)
          normalized (normalize-16-bits lower-bits)
          scale 10]
      {:gain (* normalized scale)}))

  (set-fx-parameters [this csound-instance genotype]
    (let [params (genotype->fx-parameters this genotype)
          gain (:gain params)]
      (csound/set-channel-value csound-instance "gain" gain)))

  (mutate-genotype [this genotype]
    (flip-random-bit genotype :size 16))

  (phenotype-fn [this csound-instance affected-file output-file]
    (phenotype-fn this csound-instance affected-file output-file 0 0xDEADBEEF))

  (phenotype-fn
    [this csound-instance affected-file output-file skip-time duration]
    (memoize
      (fn [genotype]
        (process this csound-instance genotype affected-file output-file
                 skip-time duration)
        (csound/analyze csound-instance output-file))))

  (process [this csound-instance genotype affected output]
    (process this csound-instance genotype affected output 0 0xDEADBEEF))

  (process [this csound-instance genotype affected output skip-time duration]
    (set-fx-parameters this csound-instance genotype)
    (csound/process csound-instance 3 affected output
                    :skip-time skip-time :duration duration))

  (process [this csound-instance genotype affected output skip-time duration
            callback interval]
    (set-fx-parameters this csound-instance genotype)
    (csound/process csound-instance 3 affected output
                    :skip-time skip-time :duration duration
                    :callback callback :interval interval)))

(defrecord BPRingModulation []
  Instrument
  (genotype->fx-parameters [this genotype]
    (let [[v1 v2 v3 v4] (long->quadruple genotype)
          mod-freq (+ (* (normalize-16-bits v1) 9950) 50)
          mod-depth (normalize-16-bits v2)
          filter-freq (+ (* (normalize-16-bits v3) 9950) 50)
          v3-normalized (normalize-16-bits v3)
          filter-bw (+ (* (double (/ (bit-shift-right v3 8) 0xFF)) 9950) 50)
          post-gain (* (double (/ (bit-and v3 0xFF) 0xFF)) 100)]
      {:modulation-freq mod-freq
       :modulation-depth mod-depth
       :filter-freq filter-freq
       :filter-bandwidth filter-bw
       :post-gain post-gain}))

  (set-fx-parameters [this csound-instance genotype]
    (let [params (genotype->fx-parameters this genotype)
          mod-freq (:modulation-freq params)
          mod-depth (:modulation-depth params)
          filter-freq (:filter-freq params)
          filter-bw (:filter-bandwidth params)
          post-gain (:post-gain params)]
      (csound/set-channel-value csound-instance "mod-freq" mod-freq)
      (csound/set-channel-value csound-instance "mod-depth" mod-depth)
      (csound/set-channel-value csound-instance "filter-freq" filter-freq)
      (csound/set-channel-value csound-instance "filter-bandwidth" filter-bw)
      (csound/set-channel-value csound-instance "post-gain" post-gain)))

  (mutate-genotype [this genotype]
    (let [mutations (+ 1 (rand-int 4))
          delta (/ 1 mutations)]
      (loop [remaining mutations
             p 1
             new-genotype genotype]
        (if (zero? remaining)
          new-genotype
          (if (< (rand) p)
            (recur (dec remaining) (- p delta) (flip-random-bit new-genotype))
            (recur (dec remaining) (- p delta) new-genotype))))))

  (phenotype-fn [this csound-instance affected-file output-file]
    (phenotype-fn this csound-instance affected-file output-file 0 0xDEADBEEF))

  (phenotype-fn
    [this csound-instance affected-file output-file skip-time duration]
    (memoize
      (fn [genotype]
        (process this csound-instance genotype affected-file
                 output-file skip-time duration)
        (csound/analyze csound-instance output-file))))

  (process [this csound-instance genotype affected output]
    (process this csound-instance genotype affected output 0 0xDEADBEEF))

  (process [this csound-instance genotype affected output skip-time duration]
    (set-fx-parameters this csound-instance genotype)
    (csound/process csound-instance 4 affected output
                    :skip-time skip-time :duration duration))

  (process [this csound-instance genotype affected output skip-time duration
            callback interval]
    (set-fx-parameters this csound-instance genotype)
    (csound/process csound-instance 4 affected output
                    :skip-time skip-time :duration duration
                    :callback callback :interval interval)))

(def effects
  "A map of effect names to their constructors."
  {"lp-distortion" ->LPDistortion
   "bp-ringmod" ->BPRingModulation 
   "gain" ->Gain})
