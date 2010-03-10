(ns coffee.core
  (:import (java.util Date))
  (:use [coffee.random]))

(def *time* nil)

(defstruct event :action :time)

(def event-comparator 
     (comparator #(.before (:time %1) (:time %2))))

(def millis-per-hour (* 60 60 1000))

(defn dt [date dtmillis]
  (Date. (+ (.getTime date) dtmillis)))

(defn in [millis]
  (dt *time* millis))

(defn evt [action time]
  (struct event action (in time)))

(defn end [time]
  (evt nil time))

(defn concat-sort [to from]
  (cond (nil? from) to
	(seq? from) (sort event-comparator (concat to from))
	't (sort event-comparator (cons from to))))

(defn execute 
  [[event & events]]
  (when (not (nil? event))
    (binding [*time* (:time event)]
;      (println "Time: " (.getTime *time*))
      (let [action (:action event)]
	(when (not (nil? action))
	  (recur (concat-sort events (action))))))))

(let [customer-queue (ref 0)]
  (defn status [message]
    (println message "-" @customer-queue "-" *time*))

  (defn customer []
    (evt #(do 
	    (if (>= @customer-queue 5)
	      (status "Customer Balked  ")
	      (do
		(dosync (ref-set customer-queue (inc @customer-queue)))
		(status "Customer Arrived ")))
	    (customer))
	 (int (* millis-per-hour (exp-rand 10)))))

  (defn barista []
    (evt #(do
	    (when (> @customer-queue 0)
	      (dosync (ref-set customer-queue (dec @customer-queue)))
	      (status "Serviced Customer"))
	    (barista))
	 (int (* millis-per-hour (exp-rand 11))))))

(defn -main []
  (binding [*time* (Date.)]
    (execute [(evt customer 0)
	      (evt barista 0)
	      (end (* 1 millis-per-hour))])))	      

(when *command-line-args*
  (-main))
