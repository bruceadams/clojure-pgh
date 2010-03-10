(ns coffee.random)

(defn ln [x]
  (Math/log x))

(defn exp-rand [a]
  (-> (rand)
      ln
      -
      (/ a)))
;  (/ (- (ln (rand))) a))

;(defn gaus-rand [a b]
;  (+ a (* b (.nextGaussian (Random. )))))
