;; Using a pod from babashka
;; Requires a pod binary — this uses the built-in test pod

(require '[babashka.pods :as pods])

;; Load the pod registry's example: AWS pod
;; (pods/load-pod 'org.babashka/aws "0.1.2")

;; Or load a local pod binary:
;; (pods/load-pod "./echo-pod")

;; After loading, the pod's namespaces are available:
;; (require '[echo.api :as echo])
;; (echo/echo "hello from the pod")
;; (echo/reverse-echo "babashka")

;; The pod protocol uses bencode over stdin/stdout.
;; Messages look like:
;;   {"op" "describe"}               -> pod reports its namespaces
;;   {"op" "invoke" "var" "ns/fn"}   -> call a function
;;   {"op" "shutdown"}               -> stop the pod

(println "Pod protocol lab")
(println "See echo_pod.clj for a pod server implementation")
(println "See NOTES.org for pod registry and protocol docs")
