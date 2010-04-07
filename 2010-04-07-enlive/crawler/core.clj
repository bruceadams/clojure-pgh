(ns crawler.core
  #^{:author "Chris Currivan"
    :doc "Code for crawling Reddit and Hacker News."}
  (:refer-clojure)
  (:use
  [clojure.set]
  [clojure.contrib.pprint]
  [clojure.stacktrace]
  [compojure.core]
  [ring.adapter.jetty]
  [ring.middleware.stacktrace])
  (:require
  [clojure.contrib.string :as string]
  [net.cgrand.enlive-html :as html]
  [com.twinql.clojure.http :as http])
  (:import
  java.util.Date))

(set! *warn-on-reflection* true)


; crawl/scrape code

(def *hn-url* "http://news.ycombinator.com/")

(def *reddit-url* "http://www.reddit.com/")

(def *hn-selector*
    [:td.title :a])

(def *reddit-title-selector*
    [:div#siteTable.sitetable.linklisting
      :p.title
      :> :a])          ; only direct children of p.title

(def *reddit-points-selector*
    [:div#siteTable.sitetable.linklisting
      :div.score.unvoted])


(defn get-url
  "Download html from a url."
  [url]
  (html/html-resource (java.net.URL. url)))

(defn get-with-user-agent
  "Crawl page, specifying user agent, or defaulting to Chrome."
  ([url]
    (let [user-agent "Mozilla/5.0 (X11; U; Linux x86_64; en-US)
AppleWebKit/532.9 (KHTML, like Gecko) Chrome/5.0.307.11 Safari/532.9"]
      (get-with-user-agent url user-agent)))
  ([url user-agent]
    (:content (http/get url
                        :as :string
                        :headers {"user-agent" user-agent}))))

(defn sort-headlines-points
  "Sort seq of vecs of [headline points] by points descending."
  [s]
  (reverse (sort-by last s)))

(defn safe-parse-int
  "Try to parse integer, or return 0."
  [x]
  (try
  (Integer/parseInt x)
  (catch NumberFormatException e
    0)))

(defn hn-headlines
  ([] (hn-headlines (get-url *hn-url*)))
  ([page] (map html/text (html/select page *hn-selector*))))

(defn hn-points
  ([] (hn-points (get-url *hn-url*)))
  ([page] (map
          #(-> %
                html/text
                ((fn [s] (re-find #"\d+" s)))
                Integer/parseInt)
          (html/select page [:td.subtext html/first-child]))))

(defn scrape-hn
  "Scrapes HackerNews first page and returns vecs of [headline points]."
  []
  (sort-headlines-points
  (zipmap (hn-headlines)
          (hn-points))))

(defn reddit-headlines
  "Crawls reddit and extracts headlines."
  ([] (reddit-headlines (get-url *reddit-url*)))
  ([page] (map html/text
              (html/select page *reddit-title-selector*))))

(defn reddit-points
  "Crawls reddit and extracts points."
  ([] (reddit-points (get-url *reddit-url*)))
  ([page]
    (map #(-> %
              html/text
              safe-parse-int)
          (html/select page *reddit-points-selector*))))

(defn scrape-reddit
  "Scrapes Reddit main page and returns vecs of [headline points]."
  []
  (sort-headlines-points
  (zipmap (reddit-headlines)
          (reddit-points))))


; ring/jetty web server code

(defn build-page
  "Make html page from scraped links."
  ([min-points data] (build-page (filter #(>= (second %) min-points) data)))
  ([data] (string/join "<br>" (map (fn [[h p]] (format "%4d %s" p h)) data))))

(defroutes main-routes
  (GET "/" []
      (build-page (sort-headlines-points
                    (concat (scrape-reddit)
                            (scrape-hn)))))
  (GET "/reddit" [min-points]
      (build-page (safe-parse-int min-points) (scrape-reddit)))
  (GET "/hacker-news" [min-points]
      (build-page (safe-parse-int min-points) (scrape-hn)))
  (ANY "*" []
      "Hello world!"))

(def app
    (->
      #'main-routes
      (wrap-stacktrace)))

(defn start-server []
  (run-jetty app {:port 8080 :join? false}))

; jetty commands
; (def server (start-server))
; (. server (stop))
