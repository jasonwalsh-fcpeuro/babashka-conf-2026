;;; babashka-conf-2026.el --- Project helpers for babashka-conf-2026 -*- lexical-binding: t; -*-

(require 'project)

(defvar babashka-conf-2026-root
  (file-name-directory (or load-file-name buffer-file-name))
  "Root directory of babashka-conf-2026 project.")

(defun babashka-conf-2026--lab-dirs ()
  "Return list of lab directories."
  (seq (directory-files
        (expand-file-name "labs" babashka-conf-2026-root)
        t "^[0-9]")))

(defun babashka-conf-2026--example-files ()
  "Return list of example scripts."
  (seq (directory-files
        (expand-file-name "examples" babashka-conf-2026-root)
        t "^[0-9]")))

(defun babashka-conf-2026-run-lab (lab-dir file)
  "Run a lab file with bb in LAB-DIR."
  (interactive
   (let* ((labs (babashka-conf-2026--lab-dirs))
          (lab (completing-read "Lab: " (mapcar #'file-name-nondirectory labs) nil t))
          (dir (expand-file-name lab (expand-file-name "labs" babashka-conf-2026-root)))
          (files (directory-files dir nil "\\.clj$"))
          (file (completing-read "File: " files nil t)))
     (list dir file)))
  (let ((default-directory lab-dir))
    (compile (format "bb %s" (shell-quote-argument file)))))

(defun babashka-conf-2026-run-example (example)
  "Run an example script."
  (interactive
   (let* ((examples (babashka-conf-2026--example-files))
          (choice (completing-read "Example: "
                                   (mapcar #'file-name-nondirectory examples)
                                   nil t)))
     (list (expand-file-name choice
                             (expand-file-name "examples" babashka-conf-2026-root)))))
  (let ((default-directory (file-name-directory example)))
    (compile (format "bb %s --help" (shell-quote-argument example)))))

(defun babashka-conf-2026-cider-jack-in-lab (lab)
  "Start a CIDER nREPL in a lab directory."
  (interactive
   (let* ((labs (babashka-conf-2026--lab-dirs))
          (choice (completing-read "Lab: " (mapcar #'file-name-nondirectory labs) nil t)))
     (list (expand-file-name choice (expand-file-name "labs" babashka-conf-2026-root)))))
  (let ((default-directory lab))
    (cider-jack-in-clj '(:jack-in-cmd "bb nrepl-server"))))

(defun babashka-conf-2026-open-notes ()
  "Open the conference notes."
  (interactive)
  (find-file (expand-file-name "NOTES.org" babashka-conf-2026-root)))

(defvar babashka-conf-2026-command-map
  (let ((map (make-sparse-keymap)))
    (define-key map (kbd "l") #'babashka-conf-2026-run-lab)
    (define-key map (kbd "e") #'babashka-conf-2026-run-example)
    (define-key map (kbd "j") #'babashka-conf-2026-cider-jack-in-lab)
    (define-key map (kbd "n") #'babashka-conf-2026-open-notes)
    map)
  "Keymap for babashka-conf-2026 commands.")

(provide 'babashka-conf-2026)
;;; babashka-conf-2026.el ends here
