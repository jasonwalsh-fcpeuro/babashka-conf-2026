;; Normalized state — Om.Next pattern
;; David Nolen's keynote: reduce state space by eliminating duplicate nested data

;; Nested (denormalized) — the same speaker appears in multiple places
(def nested-state
  {:schedule
   [{:time "13:45"
     :talk {:title "Scripting with SCI"
            :speaker {:name "Adrian Smith" :github "phronmophobic"}}}
    {:time "14:15"
     :talk {:title "Friendly CLI Tools"
            :speaker {:name "Arne Brasseur" :github "plexus"}}}
    {:time "14:30"
     :talk {:title "Charming TUIs"
            :speaker {:name "Timo Kramer" :github "TimoKramer"}}}]
   :speakers
   [{:name "Adrian Smith" :github "phronmophobic"}
    {:name "Arne Brasseur" :github "plexus"}
    {:name "Timo Kramer" :github "TimoKramer"}]})

;; Normalized — entities stored by identity, references replace nesting
(defn normalize [state]
  (let [speakers (->> (:schedule state)
                      (map #(get-in % [:talk :speaker]))
                      (into {} (map (fn [s] [(:github s) s]))))]
    {:entities
     {:speakers speakers
      :talks    (->> (:schedule state)
                     (into {}
                           (map (fn [slot]
                                  (let [talk (:talk slot)]
                                    [(:title talk)
                                     (-> talk
                                         (assoc :speaker-ref
                                                [:speakers (get-in talk [:speaker :github])])
                                         (dissoc :speaker)
                                         (assoc :time (:time slot)))])))))}
     :schedule (mapv #(get-in % [:talk :title]) (:schedule state))}))

;; Denormalize — resolve references back to nested form
(defn denormalize [norm-state]
  {:schedule
   (mapv (fn [title]
           (let [talk (get-in norm-state [:entities :talks title])
                 [table id] (:speaker-ref talk)
                 speaker (get-in norm-state [:entities table id])]
             {:time (:time talk)
              :talk (-> talk
                        (dissoc :speaker-ref :time)
                        (assoc :title title :speaker speaker))}))
         (:schedule norm-state))})

(println "--- Nested (denormalized) ---")
(clojure.pprint/pprint nested-state)

(println "\n--- Normalized ---")
(def norm (normalize nested-state))
(clojure.pprint/pprint norm)

(println "\n--- Round-trip (denormalized again) ---")
(clojure.pprint/pprint (denormalize norm))

(println "\n--- Update one speaker, affects all references ---")
(def updated (assoc-in norm [:entities :speakers "plexus" :name] "Arne Brasseur (Gaiwan)"))
(clojure.pprint/pprint (denormalize updated))
