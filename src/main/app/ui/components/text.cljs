(ns app.ui.components.text
  (:require
    ["react" :as react]
    ["react-native" :refer [Text Animated]]
    [app.lib :refer [defnc $]]
    [app.tailwind :refer [get-color]]
    [oops.core :refer [oget]]))

(def AnimatedView (oget Animated :View))

(defnc H1 [{:keys [children style]}]
  ($ Text
     {:style [{:line-height 48
               :font-size 40
               :font-weight "800"}
              style]}
     children))

(defnc H2 [{:keys [children style]}]
  ($ Text 
     {:allowFontScaling false
      :style [{:line-height 38
               :font-size 32
               :font-weight "800"}
              style]}
     children))

(defnc H3 [{:keys [children style]}]
  ($ Text
     {:style [{:line-height 22
               :font-size 22
               :font-weight "800"}
              style]}
     children))

(defnc H4 [{:keys [children style]}]
  ($ Text
     {:style [{:line-height 18
               :font-size 18
               :font-weight "800"}
              style]}
     children))

(defnc BodyMicro [{:keys [children style]}]
  ($ Text
     {:style [{:line-height 18
               :font-size 14
               :color (get-color :gray)}
              style]}
     children))

(defnc BodyRegular [{:keys [children style]}]
  ($ Text
     {:style [{:line-height 22
               :font-size 17
               :color (get-color :gray)}
              style]}
     children))

(defnc Subtitle [{:keys [children style]}]
       ($ Text
          {:style [{:font-size 17
                    :line-height 22}
                   style]}
          children))

(defnc FormTitle [{:keys [text-style animation-style text]}]
       ($ AnimatedView {:style animation-style}
          ($ Text
             {:style [{:font-size 20
                       :line-height 24
                       :font-weight "bold"}
                      text-style]}
             text)))
