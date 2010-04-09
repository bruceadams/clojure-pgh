(ns crawler.core
  #^{:author "Chris Currivan"
     :doc "Code for crawling Reddit and Hacker News."}
  (:refer-clojure)
  (:use
   [clojure.set]
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


;; crawl/scrape code

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
  (reverse (sort-by second s)))

(defn safe-parse-int
  "Try to parse integer, or return 0."
  [x]
  (try
   (Integer/parseInt x)
   (catch NumberFormatException e
     0)))

(defn hn-headlines
  "Crawls HackerNews and extracts headlines."
  ([] (hn-headlines (get-url *hn-url*)))
  ([page] (map html/text (html/select page *hn-selector*))))

(defn hn-points
  "Crawls HackerNews and extracts points."
  ([] (hn-points (get-url *hn-url*)))
  ([page] (map
           #(-> %
                html/text
                ((fn [s] (re-find #"\d+" s)))
                Integer/parseInt)
           (html/select page [:td.subtext html/first-child]))))

(defn hn-hrefs
  "Crawls HackerNews and extracts hrefs."
  ([] (hn-hrefs (get-url *hn-url*)))
  ([page] (map #(get-in % [:attrs :href]) (html/select page *hn-selector*))))

(defn scrape-hn
  "Scrapes HackerNews first page and returns vecs of [headline points]."
  ([]
     (scrape-hn (get-url *hn-url*)))
  ([page]
     (sort-headlines-points
      (map vector
           (hn-headlines page)
           (hn-points page)
           (hn-hrefs page)))))

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

(defn reddit-hrefs
  "Crawls reddit and extracts hrefs."
  ([] (reddit-headlines (get-url *reddit-url*)))
  ([page] (map #(get-in % [:attrs :href])
               (html/select page *reddit-title-selector*))))

(defn scrape-reddit
  "Scrapes Reddit main page and returns vecs of [headline points hrefs]."
  ([]
     (scrape-reddit (get-url *reddit-url*)))
  ([page]
     (sort-headlines-points
      (map vector
           (reddit-headlines page)
           (reddit-points page)
           (reddit-hrefs page)))))

(defn scrape-both
  "Scrape both Reddit and HackerNews, and berge the results."
  []
  (sort-headlines-points
   (concat (scrape-reddit)
           (scrape-hn))))


;; enlive templating code

;; snippet template for each link
(html/defsnippet link-model "/Users/josephgallo/Documents/Work/Personal/Projects/clojure-pgh/2010-04-07-enlive/crawler/html/link.html" [:td]
  [{points :points text :text href :href}]
  [:td.points] (html/do-> (html/content (str points)))
  [:a] (html/do->
        (html/content text)
        (html/set-attr :href href)))

;; template for page
(html/deftemplate merged-template "/Users/josephgallo/Documents/Work/Personal/Projects/clojure-pgh/2010-04-07-enlive/crawler/html/merged.html" [header items]
  [:title] (html/content header)
  [:h1] (html/content header)
  [:table :tr] (html/clone-for
                [item items]
                (html/content (link-model {:points (second item)
                                           :text (first item)
                                           :href (nth item 2 "")}))))


;; Compojure method to map requests to handler code.
(defroutes main-routes
  (GET "/" []
       (apply str
              (merged-template "Reddit + HackerNews" (scrape-both))))
  (GET "/reddit" []
       (apply str
              (merged-template "Reddit" (scrape-reddit))))
  (GET "/hacker-news" []
       (apply str
              (merged-template "HackerNews" (scrape-hn))))
  (ANY "*" []
       "Hello world!"))


;; ring web server code

;; Wrap routes with a fn that serves stacktrace on error.
(def app
     (->
      #'main-routes
      (wrap-stacktrace)))

(defn start-server []
  (run-jetty app {:port 8080 :join? false}))

;; jetty commands:

;; (def server (start-server))
;; (. server (stop))
