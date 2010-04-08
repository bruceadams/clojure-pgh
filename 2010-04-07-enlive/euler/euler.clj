(defn evenly?
  ([x] true)
  ([x y] (zero? (mod x y)))
  ([x y & z] (and (evenly? x y)
                  (apply evenly? (cons x z)))))

(println
 "Euler #1 :"
 (reduce + (filter #(or (evenly? % 3) (evenly? % 5))
                   (range 1 1000))))

(defn fib-seq []
  ((fn rfib [a b]
     (cons a (lazy-seq (rfib b (+ a b)))))
   0 1))

(println
 "Euler #2 :"
 (reduce + (filter even? (take-while #(< % 4000000) (fib-seq)))))

(defn candidates [x]
  (filter #(evenly? x %)
          (take-while #(<= % (int (Math/sqrt x)))
                      (iterate inc 2))))

(defn factors [x]
  (let [c (candidates x)]
    (sort (into c
                (map #(/ x %) c)))))

(defn prime? [x]
  (empty? (factors x)))

(defn prime-factors [x]
  (filter prime? (factors x)))

(defn factorization [x]
  (let [f (prime-factors x)]
    (if (empty? f)
      [x]
      (cons (first f)
            (lazy-seq (factorization (/ x (first f))))))))

(println
 "Euler #3 :"
 (last (factorization 600851475143)))

(defn palindrome? [s]
  (let [n (str s)]
    (= (seq n) (reverse n))))

(def threes (range 999 99 -1))

(println
 "Euler #4 :"
 (reduce max (filter palindrome?
                     (mapcat #(map * (repeat %1) %2)
                             threes
                             (repeat threes)))))

(defn gcd [x y]
  (let [a (min x y)
        b (max x y)]
    (if (zero? a)
      b
      (recur a (- b a)))))

(defn lcm
  ([x y] (/ (* x y) (gcd x y)))
  ([x y & more] (reduce lcm (lcm x y) more)))

(println
 "Euler #5 :"
 (apply lcm (range 2 21)))
