(ns app.ui.screens.onboarding
  (:require ["react-native" :refer [View TouchableOpacity Animated]]
            ["react-native-keyboard-aware-scroll-view" :refer [KeyboardAwareScrollView]]
            ["react-native-snap-carousel" :refer [default Pagination] :rename {default SnapCarousel}]
            ["lottie-react-native" :as LottieView]
            ["expo-av" :refer [Video]]
            [cljs-bean.core :refer [->clj]]
            [keechma.next.helix.core :refer [with-keechma]]
            [helix.hooks :as hooks]
            [app.hooks :refer [use-dimensions]]
            [app.lib :refer [$ defnc convert-style]]
            [app.tailwind :refer [tw]]
            [app.rn.navigation :refer [navigate]]
            [app.ui.components.screen-container :refer [ScreenContainer]]
            [app.ui.components.buttons :as buttons]
            [app.ui.components.text :as text]
            [app.rn.animated :as a]
            [oops.core :refer [ocall]]))

(def common-timing {:to-value 1 :duration 500 :delay 300 :easing (a/easing :ease)})
(def common-top-interpolate-params {:output-range [150 0]})

(defnc PersonButtonView [{:keys [navigation register-button-animate]}]
  ($ a/View {:style [{:opacity @register-button-animate
                      :top (a/interpolate @register-button-animate common-top-interpolate-params)}
                     (tw :w-full :justify-center :items-center :mt-10)]}
     ($ buttons/Big
        {:title "Osobe"
         :onPress #(navigate navigation "person")})))

(defnc ProjectButtonView [{:keys [navigation register-button-animate]}]
       ($ a/View {:style [{:opacity @register-button-animate
                           :top (a/interpolate @register-button-animate common-top-interpolate-params)}
                          (tw :w-full :justify-center :items-center :mt-10)]}
          ($ buttons/Big
             {:title "Projekti"
              :onPress #(navigate navigation "project")})))

(defnc RoleButtonView [{:keys [navigation register-button-animate]}]
       ($ a/View {:style [{:opacity @register-button-animate
                           :top (a/interpolate @register-button-animate common-top-interpolate-params)}
                          (tw :w-full :justify-center :items-center :mt-10)]}
          ($ buttons/Big
             {:title "Uloge"
              :onPress #(navigate navigation "role")})))


(defnc ScreenRenderer [props]
  (let [register-button-animate (hooks/use-ref (a/value 0))
        signin-area-animate (hooks/use-ref (a/value 0))
        on-render-animate (hooks/use-ref (ocall Animated :stagger 200
                                                #js [(a/timing @register-button-animate common-timing)
                                                     (a/timing @signin-area-animate common-timing)]))]

    (hooks/use-effect :once (-> @on-render-animate
                                (a/start)))
    ($ ScreenContainer
       ($ KeyboardAwareScrollView {:style {:flex 1}}
        ($ View {:style [(tw :items-center :justify-center :px-12 :py-48 :w-full :h-full)]}
           ($ PersonButtonView {:register-button-animate register-button-animate & props})
           ($ ProjectButtonView {:register-button-animate register-button-animate & props})
           ($ RoleButtonView {:register-button-animate register-button-animate & props}))))))

(def Screen (with-keechma ScreenRenderer))
