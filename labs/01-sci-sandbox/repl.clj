(require '[sci.core :as sci])

;; Tiny REPL backed by SCI — persistent context
(let [ctx (sci/init {:namespaces {'user {}}})]
  (print "sci> ") (flush)
  (loop []
    (when-let [line (read-line)]
      (when-not (= line "exit")
        (try
          (println (sci/eval-string* ctx line))
          (catch Exception e
            (println "ERROR:" (.getMessage e))))
        (print "sci> ") (flush)
        (recur)))))
