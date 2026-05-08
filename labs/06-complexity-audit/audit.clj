;; Complexity audit — measure interleaving in code
;; Inspired by Josh Glover's "Easy Made Complex" talk
;; Rich Hickey's definition: complexity = things braided together

(require '[clojure.java.io :as io]
         '[clojure.string :as str])

(defn count-interleaving
  "Count indicators of interleaved concerns in a source file."
  [file]
  (let [lines    (str/split-lines (slurp file))
        patterns {:conditionals #"(if|when|cond|case)\b"
                  :mutations    #"(swap!|reset!|set!|alter|def\s)"
                  :io           #"(println|slurp|spit|read-line|io/)"
                  :exceptions   #"(try|catch|throw)"
                  :state        #"(atom|ref|agent|volatile!)"}]
    {:file    (str file)
     :lines   (count lines)
     :counts  (into {}
                    (map (fn [[k pat]]
                           [k (count (filter #(re-find pat %) lines))])
                         patterns))
     :density (let [total (reduce + (map (fn [[_ pat]]
                                           (count (filter #(re-find pat %) lines)))
                                         patterns))]
                (if (pos? (count lines))
                  (double (/ total (count lines)))
                  0.0))}))

(defn audit-directory [dir]
  (->> (file-seq (io/file dir))
       (filter #(str/ends-with? (str %) ".clj"))
       (map count-interleaving)
       (sort-by :density >)))

;; Run against the labs themselves
(let [results (audit-directory ".")]
  (println (format "%-40s %5s %8s" "File" "Lines" "Density"))
  (println (str/join (repeat 55 "-")))
  (doseq [r results]
    (println (format "%-40s %5d %8.3f"
                     (last (str/split (:file r) #"/"))
                     (:lines r)
                     (:density r))))
  (println)
  (when-let [worst (first results)]
    (println "Highest interleaving:" (:file worst))
    (println "  Breakdown:" (:counts worst))))
