;; Elm Architecture TUI — todo list
;; j/k to navigate, space to toggle, d to delete, a to add, q to quit

(import '[org.jline.terminal TerminalBuilder])

(defn init []
  {:items [{:text "Learn SCI" :done false}
           {:text "Build a pod" :done false}
           {:text "Write a TUI" :done false}]
   :cursor 0})

(defn update-model [model msg]
  (let [n (count (:items model))]
    (case (:type msg)
      :down   (update model :cursor #(min (dec n) (inc %)))
      :up     (update model :cursor #(max 0 (dec %)))
      :toggle (update-in model [:items (:cursor model) :done] not)
      :delete (-> model
                  (update :items #(into (subvec % 0 (:cursor model))
                                        (subvec % (inc (:cursor model)))))
                  (update :cursor #(min % (- n 2))))
      :add    (update model :items conj {:text (:text msg) :done false})
      model)))

(defn view [model]
  (str "\033[2J\033[H"
       "Todo (j/k move, space toggle, d delete, a add, q quit)\n\n"
       (->> (:items model)
            (map-indexed
             (fn [i item]
               (str (if (= i (:cursor model)) " > " "   ")
                    (if (:done item) "[x] " "[ ] ")
                    (:text item))))
            (clojure.string/join "\n"))))

(let [terminal (-> (TerminalBuilder/builder) (.system true) (.build))
      reader   (.reader terminal)]
  (.enterRawMode terminal)
  (loop [model (init)]
    (print (view model))
    (flush)
    (let [ch (.read reader)]
      (condp = ch
        (int \q) (do (print "\033[2J\033[H") (flush) (.close terminal))
        (int \j) (recur (update-model model {:type :down}))
        (int \k) (recur (update-model model {:type :up}))
        (int \space) (recur (update-model model {:type :toggle}))
        (int \d) (if (pos? (count (:items model)))
                   (recur (update-model model {:type :delete}))
                   (recur model))
        (int \a) (do
                   (print "\033[2J\033[HNew item: ")
                   (flush)
                   (.close terminal)
                   (let [text (read-line)
                         terminal2 (-> (TerminalBuilder/builder) (.system true) (.build))
                         reader2 (.reader terminal2)]
                     (.enterRawMode terminal2)
                     ;; Note: re-entering raw mode requires a new terminal
                     ;; In practice, charm.clj handles this lifecycle
                     (println "Added:" text)
                     (.close terminal2)))
        (recur model)))))
