(require '[sci.core :as sci])

;; Basic eval
(sci/eval-string "(+ 1 2 3)")
(sci/eval-string "(map inc [1 2 3])")

;; Persistent context — state survives across evals
(let [ctx (sci/init {})]
  (sci/eval-string* ctx "(def counter (atom 0))")
  (sci/eval-string* ctx "(swap! counter inc)")
  (sci/eval-string* ctx "(swap! counter inc)")
  (sci/eval-string* ctx "@counter"))

;; Namespace allowlisting — expose a custom API
(let [my-api {'greet (fn [name] (str "Hello, " name "!"))
              'upper (fn [s] (clojure.string/upper-case s))}
      ctx (sci/init {:namespaces {'api my-api}})]
  (sci/eval-string* ctx "(api/greet \"Babashka\")")
  (sci/eval-string* ctx "(api/upper \"clojure\")"))

;; Sandbox boundaries — these should fail
(try (sci/eval-string "(System/exit 0)")
     (catch Exception e (println "Blocked:" (.getMessage e))))

(try (sci/eval-string "(slurp \"/etc/passwd\")")
     (catch Exception e (println "Blocked:" (.getMessage e))))
