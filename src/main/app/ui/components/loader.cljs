(ns app.ui.components.loader
  (:require ["react-native" :refer [View Text ActivityIndicator]]
            [app.lib :refer [$ defnc]]
            [app.tailwind :refer [tw]]))

(defnc Loader [{:keys [style] :as props}]
  ($ View {:style [(tw :flex :flex-1 :items-center :justify-center)
                   style]}
     ($ ActivityIndicator {:size "large"
                           :color "#999999"})))