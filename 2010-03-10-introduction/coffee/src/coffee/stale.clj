(ns coffee.stale
  (:import (java.util Date PriorityQueue)))

(def *time* nil)

(defstruct event :time :action)

(def event-comparator 
     (comparator #(.before (:time %1) (:time %2))))

(def event-queue (PriorityQueue. 10 event-comparator))

(defn dt [date dtmillis]
  (Date. (+ (.getTime date) dtmillis)))

(defn enq-event [action time]
  (.add event-queue (struct event (dt *time* time) action)))

(defn execute-events []
  (while (not (empty? event-queue))
	 (let [e (.poll event-queue)]
	   (binding [*time* (:time e)]
	     (println "Time: " (.getTime *time*))
	     ((:action e))))))

(defn -main []
  (binding [*time* (Date.)]
    (enq-event #(enq-event (fn [] (println 6)), 6000) 1000)
    (enq-event #(println 2) 2000)
    (enq-event #(println 3) 3000)
    (enq-event #(println 4) 4000)
    (enq-event #(println 5) 5000)
    (execute-events)))

(if *command-line-args*
  (-main))
