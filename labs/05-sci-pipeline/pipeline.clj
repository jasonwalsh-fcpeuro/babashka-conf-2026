;; SCI as a data pipeline engine
;; Inspired by Flower's approach: Clojure functions as pipeline stages
;; with SCI providing sandboxed execution

(require '[sci.core :as sci])

;; Pipeline: a sequence of SCI expressions that transform data
(def stages
  ["(map #(update % :title clojure.string/upper-case) data)"
   "(filter #(> (:year %) 2020) data)"
   "(sort-by :year data)"
   "(map #(select-keys % [:title :year]) data)"])

(def sample-data
  [{:title "Babashka" :year 2019 :lang "Clojure"}
   {:title "Flower" :year 2024 :lang "Clojure"}
   {:title "charm.clj" :year 2025 :lang "Clojure"}
   {:title "Bob CD" :year 2020 :lang "Clojure"}
   {:title "Blambda" :year 2022 :lang "Clojure"}])

(defn run-pipeline [data stages]
  (let [ctx (sci/init {:namespaces {'user {'data data}}})]
    (reduce
     (fn [data stage]
       (let [ctx (sci/init {:namespaces {'user {'data data}}})]
         (sci/eval-string* ctx stage)))
     data
     stages)))

(println "Input:")
(doseq [item sample-data]
  (println " " item))

(println "\nPipeline stages:")
(doseq [[i stage] (map-indexed vector stages)]
  (println (str "  " (inc i) ". " stage)))

(println "\nOutput:")
(doseq [item (run-pipeline sample-data stages)]
  (println " " item))
