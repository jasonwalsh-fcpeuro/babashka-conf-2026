;; SCI as a template engine
;; Inline Clojure in text — the Flower approach to templates

(require '[sci.core :as sci]
         '[clojure.string :as str])

(defn render [template bindings]
  (let [ctx (sci/init {:namespaces {'user bindings}})]
    (str/replace template #"\{\{(.+?)\}\}"
                 (fn [[_ expr]]
                   (str (sci/eval-string* ctx (str/trim expr)))))))

(def page-template
  "<h1>{{title}}</h1>
<p>By {{author}} | {{(str year)}}</p>
<ul>
{{(apply str (map #(str \"  <li>\" % \"</li>\\n\") topics))}}
</ul>
<p>Generated: {{(str (java.time.LocalDate/now))}}</p>")

(println
 (render page-template
         {'title  "Babashka Conf 2026"
          'author "Michiel Borkent"
          'year   2026
          'topics ["SCI" "GraalVM" "CLI tooling" "TUI" "Pods"]}))
