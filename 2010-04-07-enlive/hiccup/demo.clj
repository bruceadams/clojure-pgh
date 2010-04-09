(ns demo
  (:require
   [hiccup.core :as hiccup]))

;; gives you a nice idiomatic syntax for building html from sexprs
(def mypage 
     (hiccup/html 
      [:html
       [:head
	[:title "Hello, world!"]
	[:script {:type "text/javascript"} "alert('Hi!')"]]
       [:body
	[:h1#headline "This is a heading!"]
	[:p.red "This is a paragraph"]]]))

;; and you can write functions that build html as templates
(defn headline [message]
  (hiccup/html [:h1.headline message]))
;; the hiccup/html macro pre-compiles the html, it doesn't rebuild 
;; it on every invocation

(defn template [title, message, body]
  (hiccup/html
   [:html
    [:head
     [:title title]]
    [:body
     (headline message)
     body]]))

(template "My Title" "My Message" (hiccup/html [:p "Content here"]))
