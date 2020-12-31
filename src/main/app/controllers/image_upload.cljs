(ns app.controllers.image-upload
  (:require [keechma.next.controller :as ctrl]
            [keechma.next.controllers.pipelines :as pipelines]
            [keechma.pipelines.core :as pp :refer-macros [pipeline!]]
            [keechma.next.controllers.form :as form]
            [oops.core :refer [ocall oget oset!]]
            [keechma.next.controllers.router :as router]
            [app.validators :as v]
            ["expo-image-picker" :as ImagePicker]))

(derive :image-upload ::pipelines/controller)

(def image-media-type (-> (oget ImagePicker :MediaTypeOptions)
                          (oget :Images)))

(def pipelines
  {:permission
   (pipeline! [value {:keys [state*] :as ctrl}]
              (ocall ImagePicker :getCameraRollPermissionsAsync)
              (if (not= "granted" (:status (js->clj value :keywordize-keys true)))
                (pipeline! [value ctrl]
                           (ocall ImagePicker :requestCameraRollPermissionsAsync)
                           (when (:granted (js->clj value :keywordize-keys true))
                                 (ctrl/dispatch ctrl :image-upload :upload-image)))
                (ctrl/dispatch ctrl :image-upload :upload-image)))

   :upload-image
   (pipeline! [value {:keys [state*] :as ctrl}]
              (ocall ImagePicker :launchImageLibraryAsync #js{:mediaTypes image-media-type
                                                              :base64 true})
              (when (not (:cancelled (js->clj value :keywordize-keys true)))
                (pp/swap! state* assoc :uri (:uri (js->clj value :keywordize-keys true)))
                (pp/swap! state* assoc :base-64 (:base64 (js->clj value :keywordize-keys true)))))})

(defmethod ctrl/start :image-upload [_ _ _ _]
  {:uri nil
   :base-64 nil})

(defmethod ctrl/prep :image-upload [ctrl]
  (-> ctrl
      (pipelines/register pipelines)))

(defmethod ctrl/derive-state :image-upload [_ state _]
  state)