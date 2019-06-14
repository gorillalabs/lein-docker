(ns leiningen.docker
  (:require [leiningen.core.eval :as eval]
            [leiningen.core.main :as main]
            [clojure.java.shell :as shell]))

(defn- exec [& args]
  (apply main/debug "Exec: docker" args)
  (apply eval/sh "docker" args))

(defn- build
  "lein docker build [image-name [additional-arguments-to-build]]
     Builds image name.  
     additional-arguments are inserted into the build command line before the final path argument."
  [image dockerfile build-dir additional-args]
  (if (not-empty additional-args)
    (main/info "Building Docker image:" image "with additional args:" additional-args)
    (main/info "Building Docker image:" image))
  (let [exit-code (apply exec "build" "-f" dockerfile "-t" image (concat additional-args [build-dir]))]
    (if (zero? exit-code)
      (main/info "Docker image built.")
      (do
        (main/warn "Docker image could not be built.")
        (main/exit exit-code)))))

(defn- tag [image tagged-image]
  (main/info "Tagging Docker image:" tagged-image)
  (let [exit-code (exec "tag" image tagged-image)]
    (if (zero? exit-code)
      (main/info "Docker image tagged.")
      (do
        (main/warn "Docker image could not be tagged.")
        (main/exit exit-code)))))

(defn- push
  "lein docker push [image-name]
     Pushes image-name to its registry"
  [image]
  (main/info "Pushing Docker image:" image)
  (let [exit-code (exec "push" image)]
    (if (zero? exit-code)
      (main/info "Docker image pushed.")
      (do
        (main/warn "Docker image could not be pushed.")
        (main/exit exit-code)))))

(defn- image-exists? [image]
  (let [{:keys [exit out err]} (shell/sh "docker" "images" "-q" image)]
    (if (zero? exit)
      (boolean (not-empty out))
      (do
        (main/warn err)
        (main/exit exit)))))

(defn- rmi
  "Remove an image from the local docker system."
  [image]
  (when (image-exists? image)
    (main/info "Removing docker image:" image)
    (let [exit-code (exec "rmi" image)]
      (if (zero? exit-code)
        (main/info "Docker image removed.")
        (do
          (main/warn "Docker image could not be removed.")
          (main/exit exit-code))))))

(def valid-command? #{:build :push :rmi})

(defn docker
  "Builds and delpoys docker images.
   Commands:
     'build' builds your docker image
     'push' pushes your docker image
     'rmi' removes your docker image"
  {:subtasks [#'build #'push #'rmi]}
  [project command & [image-name & additional-args]]

  (let [command (keyword command)]

    (when-not (valid-command? command)
      (main/warn "Invalid command" command)
      (main/exit 1))

    (let [config (:docker project)
          image-name (or image-name
                         (:image-name config)
                         (str (:name project)))
          tags (or (:tags config)
                   ["%s"])
          images (map
                   #(str image-name ":" (format % (:version project)))
                   tags)
          build-dir (or (:build-dir config)
                        (:root project))
          dockerfile (or (:dockerfile config)
                         "Dockerfile")]

      (case command
        :build (do
                 (build (first images) dockerfile build-dir additional-args)
                 (doseq [image (rest images)]
                   (tag (first images) image)))
        :push (doseq [image images]
                (push image))
        :rmi (doseq [image images]
               (rmi image))))))
