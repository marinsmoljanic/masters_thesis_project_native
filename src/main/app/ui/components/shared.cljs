(ns app.ui.components.shared
  (:require ["react-native" :refer [View Text TextInput ScrollView TouchableOpacity]]
            [app.lib :refer [$ defnc]]
            [app.tailwind :refer [tw]]
            [app.ui.components.inputs :refer [wrapped-input]]
            [keechma.next.helix.core :refer [with-keechma]]
            [app.ui.components.screen-container :refer [ScreenContainer]]
            [app.ui.components.text :as text]))

(defnc MarginVRenderer [{:keys [spacing]}]
       ($ View {:style [(tw :w-full)
                        {:margin-vertical (if spacing spacing 5)}]}))

(def MarginV (with-keechma MarginVRenderer))


(defnc ButtonBigGray [{:keys [on-press style text-style title]}]
       ($ TouchableOpacity
          {:onPress on-press
           :style   [(tw :px-4 :py-3 :w-full :rounded-18 :text-sm :flex :justify-center :items-center)
                     style]}
          ($ Text {:style [(tw :font-bold)
                           {:font-size 16
                            :line-height 16}
                           text-style]} title)))

(defnc Errors [{:keys [errors]}]
  (let [issues (into [] (vals (:issues errors)))]
    ($ View {:style [(tw :flex :flex-1)]}
       ($ Text {:style [(tw :text-red :w-full :font-bold :text-sm :mt-4)]}
          (:message errors))
       (when (not-empty issues)
         ($ View
            (map #($ Text {:key % :style [(tw :text-red :w-full :font-bold :text-sm :mt-2)]}
                     (first %))
                 issues))))))
