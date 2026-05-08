;; Elm Architecture TUI — counter with JLine3 key input
;; Press +/- to change count, r to reset, q to quit

(import '[org.jline.terminal TerminalBuilder])

(defn init [] {:count 0 :history []})

(defn update-model [model msg]
  (case (:type msg)
    :inc   (-> model (update :count inc) (update :history conj "+"))
    :dec   (-> model (update :count dec) (update :history conj "-"))
    :reset (init)
    model))

(defn view [model]
  (str "Count: " (:count model)
       "  History: " (apply str (:history model))))

(let [terminal (-> (TerminalBuilder/builder) (.system true) (.build))
      reader   (.reader terminal)]
  (.enterRawMode terminal)
  (println "Press +/- to change, r to reset, q to quit\n")
  (loop [model (init)]
    (print (str "\r  " (view model) "     "))
    (flush)
    (let [ch (.read reader)]
      (condp = ch
        (int \q) (do (println) (.close terminal))
        (int \+) (recur (update-model model {:type :inc}))
        (int \-) (recur (update-model model {:type :dec}))
        (int \r) (recur (update-model model {:type :reset}))
        (recur model)))))
