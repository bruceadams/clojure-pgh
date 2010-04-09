(ns demo
  (:require
   [net.cgrand.enlive-html :as html]))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn fetch-file [file]
  (html/html-resource (java.io.File. file)))

(defn fetch-str [str]
  (html/html-resource (java.io.StringReader. str)))

;; we can fetch the content of a file, and see the underlying data
;; structure it builds
(def test-html (fetch-file "test.html"))

;; say we want to examine just a piece of that file, we can use a css
;; selector to retrieve it
(def h1 (html/select test-html [:h1]))

;; if we want the text of that node
(println (map html/text h1)) 
;; returns a list of strings, because there could be many h1s you can
;; always call (apply html/text ...) if you know there is just one

;; now, that's all well and good, but how do we use this to
;; dynamically create html?  ... with templates

;; this template will take the file test.html and
;; turn it (essentially) into a function called
;; t1, which will take a new title and a new body
;; and return a list of tokens that together
;; will make up the newly generated html
(html/deftemplate t1 (java.io.File. "test.html")
  [title body]
  [:title] (html/content title)
  [:h1] (html/content body))

;; so, let's call it with some arguments!
(println 
 (apply str (t1 "My new title!" "My new body!")))

;; nifty!
