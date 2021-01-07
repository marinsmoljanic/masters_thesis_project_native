(ns app.ui.components.buttons
  (:require
   ["react-native" :as rn :refer [TouchableOpacity]]
   ["react" :as react]
   [app.lib :refer [defnc $]]
   [app.tailwind :refer [tw]]
   [app.rn.animated :as animated]))

(def easing (animated/easing :ease))

(defnc Big [{:keys [onPress title transition-value]}]
  (let [transition-value' (or transition-value animated/constantly-1)]
    ($ TouchableOpacity
       {:onPress onPress
        :activeOpacity 0.9
        :style (tw :bg-purple :rounded-3xl :py-5 :w-full)}
       ($ animated/Text
          {:style [(tw :text-white :text-center :items-center)
                   {:font-size 16
                    :font-weight "700"
                    :opacity (animated/interpolate transition-value' {:input-range [0 0.33 0.66 1]
                                                                      :output-range [0 0 0 1]
                                                                      :easing easing})}]}
          title))))

(defnc Medium [{:keys [onPress title transition-value]}]
       (let [transition-value' (or transition-value animated/constantly-1)]
            ($ TouchableOpacity
               {:onPress       onPress
                :activeOpacity 0.9
                :style         [(tw :bg-purple :rounded-3xl :py-5)
                                {:width "40%"}]}
               ($ animated/Text
                  {:style [(tw :text-white :text-center :items-center)
                           {:font-size 16
                            :font-weight "700"
                            :opacity (animated/interpolate transition-value' {:input-range [0 0.33 0.66 1]
                                                                              :output-range [0 0 0 1]
                                                                              :easing easing})}]}
                  title))))

(defnc Small [{:keys [onPress title transition-value]}]
      ($ TouchableOpacity
         {:onPress       onPress
          :activeOpacity 0.9
          :style         [(tw :bg-purple :rounded-xl :py-2)
                          {:width "20%"}]}
         ($ animated/Text
            {:style [(tw :text-white :text-center :items-center)
                     {:font-size 13
                      :font-weight "400"}]}
            title)))

(defnc SmallDelete [{:keys [onPress title transition-value]}]
       ($ TouchableOpacity
          {:onPress       onPress
           :activeOpacity 0.9
           :style         [(tw :bg-red :rounded-xl :py-2)
                           {:width "20%"}]}
          ($ animated/Text
             {:style [(tw :text-white :text-center :items-center)
                      {:font-size 13
                       :font-weight "400"}]}
             title)))

(defnc Rounded [{:keys [onPress title transition-value]}]
       (let [transition-value' (or transition-value animated/constantly-1)]
            ($ TouchableOpacity
               {:onPress onPress
                :activeOpacity 0.9
                :style (tw :bg-purple :py-5 :rounded-full)}
               ($ animated/Text
                  {:style [(tw :text-white :text-center :items-center)
                           {:font-size 16
                            :font-weight "700"
                            :opacity (animated/interpolate transition-value' {:input-range [0 0.33 0.66 1]
                                                                              :output-range [0 0 0 1]
                                                                              :easing easing})}]}
                  title))))
