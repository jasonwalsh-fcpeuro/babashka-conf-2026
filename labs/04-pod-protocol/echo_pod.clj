;; Babashka pod protocol — build a minimal pod server
;; Pods communicate over stdin/stdout using bencode + EDN

(require '[bencode.core :as bencode]
         '[clojure.edn :as edn])

(defn write-msg [out msg]
  (bencode/write-bencode out msg)
  (.flush out))

(defn read-msg [in]
  (bencode/read-bencode in))

(let [in  (java.io.PushbackInputStream. System/in)
      out System/out]
  (loop []
    (let [msg (read-msg in)
          op  (get msg "op")]
      (case op
        "describe"
        (do (write-msg out
                       {"format"    "edn"
                        "namespaces" [{"name" "echo.api"
                                       "vars" [{"name" "echo"}
                                               {"name" "reverse-echo"}]}]})
            (recur))

        "invoke"
        (let [var-name (get msg "var")
              args     (edn/read-string (get msg "args"))
              result   (case var-name
                         "echo.api/echo"         (first args)
                         "echo.api/reverse-echo" (apply str (reverse (first args)))
                         (throw (ex-info "Unknown var" {:var var-name})))]
          (write-msg out {"value"  (pr-str result)
                          "id"     (get msg "id")
                          "status" ["done"]})
          (recur))

        "shutdown" nil

        (recur)))))
