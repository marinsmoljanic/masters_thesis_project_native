(ns app.ui.screens.home
  (:require ["react-native" :refer [View Image Text TouchableOpacity Animated]]
            ["react-native-keyboard-aware-scroll-view" :refer [KeyboardAwareScrollView]]
            ["lottie-react-native" :as LottieView]
            ["expo-web-browser" :as WebBrowser]
            ["expo-device" :as Device]
            ["expo-constants" :default Constants]
            [app.domain.providers :refer [get-external-url]]
            [oops.core :refer [oget ocall]]
            [keechma.next.helix.core :refer [with-keechma use-sub use-meta-sub]]
            [keechma.next.controllers.pipelines :refer [get-promise]]
            [clojure.string :as s]
            [helix.hooks :as hooks]
            [app.lib :refer [$ defnc]]
            [app.tailwind :refer [tw]]
            [app.rn.navigation :refer [navigate]]
            [app.ui.components.screen-container :refer [ScreenContainer]]
            [app.ui.components.loader :refer [Loader]]
            [app.ui.components.text :as text]
            [app.domain.providers :as providers]
            [app.ui.svgs :refer [Svg]]
            [app.rn.animated :as a]
            [oops.core :refer [ocall]]))

(defn get-running-device []
  (-> (oget Constants :platform)
      (js->clj)
      (keys)
      (first)))

(defn ios-version>13? [version]
  (>= version 13))

(def common-timing {:to-value 1 :duration 300 :delay 500 :easing (a/easing :ease)})
(def common-top-interpolate-params {:output-range [100 0]})

(defn get-item-height [item-count]
  (cond
    (= 1 item-count) 250
    (>= 3 item-count) 120
    :else 80))

(def item-animation-offset 200)

(def non-sso-provides '(:teladoc))

(defn get-provider-link [{:keys [item jwt] :as props}]
  (let [provider-id (:id item)
        non-sso-provider (some #{provider-id} non-sso-provides)
        member-providers (use-sub props :member-providers)
        non-sso-provider-link (->> member-providers
                                   (filter #(= (:telemedproviderid %) (:external-id item)))
                                   first
                                   :link)]
    (if non-sso-provider
      non-sso-provider-link
      (get-external-url provider-id jwt))))

(defnc SoloProviderItem [{:keys [item item-height navigation delay jwt] :as props}]
  (let [border-animation-ref (hooks/use-ref (a/value 0))
        icon-bg-animation-ref (hooks/use-ref (a/value 0))
        icon-animation-ref (hooks/use-ref (a/value 0))
        title-animation-ref (hooks/use-ref (a/value 0))
        subtitle-animation-ref (hooks/use-ref (a/value 0))
        on-render-animate (hooks/use-ref
                           (ocall Animated :sequence #js [(ocall Animated :delay delay)
                                                          (ocall Animated :stagger 100
                                                                 #js [(a/timing @border-animation-ref common-timing)
                                                                      (a/timing @icon-bg-animation-ref common-timing)
                                                                      (a/timing @title-animation-ref common-timing)
                                                                      (a/timing @subtitle-animation-ref common-timing)
                                                                      (a/timing @icon-animation-ref common-timing)])]))
        device (get-running-device)
        os-version (oget Device :osVersion)
        provider-link (get-provider-link props)
        on-press (if (and (= device "ios") (ios-version>13? os-version))
                   #(ocall WebBrowser :openBrowserAsync provider-link)
                   #(navigate navigation (:screen item)))]

    (hooks/use-effect :once (-> @on-render-animate
                                (a/start)))

    ($ View {:style [{:height item-height
                      :width 206}
                     (tw :relative :pt-12 :items-center :justify-center)]}
       ($ a/View {:style [{:opacity @border-animation-ref
                           :height item-height
                           :right (a/interpolate @border-animation-ref {:output-range [50 0]})}
                          (tw :absolute :top-0 :bottom-0 :left-0 :border-gray-light-2 :border-2 :rounded-3xl)]})
       ($ TouchableOpacity {:onPress on-press
                            :style [{:height item-height} (tw :flex-1 :items-center :justify-center)]}
          ($ a/View {:style [(tw :flex-1 :items-center :justify-center)]}
             ($ a/View {:style [{:height 60
                                 :opacity @icon-bg-animation-ref
                                 :width (a/interpolate @icon-bg-animation-ref {:output-range [0 60]})}
                                (tw (:bg-color item) :rounded-lg)]})
             ($ a/View {:style [{:opacity @icon-animation-ref
                                 :height 120
                                 :width 120}
                                (tw :absolute)]}
                ($ Image {:style [(tw :w-full :h-full)] :source (:icon item)})))
          ($ View {:style [(tw :flex-1 :items-center :justify-center)]}
             ($ a/View {:style [{:opacity @title-animation-ref
                                 :left (a/interpolate @title-animation-ref {:output-range [-20 0]})}]}
                ($ text/H4 (:title item)))
             ($ a/View {:style [{:opacity @subtitle-animation-ref
                                 :left (a/interpolate @subtitle-animation-ref {:output-range [-20 0]})}]}
                ($ text/BodyMicro (:description item))))))))

(defnc ProviderItem [{:keys [item item-height navigation delay jwt] :as props}]
  (let [border-animation-ref (hooks/use-ref (a/value 0))
        icon-bg-animation-ref (hooks/use-ref (a/value 0))
        icon-animation-ref (hooks/use-ref (a/value 0))
        title-animation-ref (hooks/use-ref (a/value 0))
        subtitle-animation-ref (hooks/use-ref (a/value 0))
        lottie-ref (hooks/use-ref nil)
        on-render-animate (hooks/use-ref
                           (ocall Animated :sequence #js [(ocall Animated :delay delay)
                                                          (ocall Animated :stagger 100
                                                                 #js [(a/timing @border-animation-ref common-timing)
                                                                      (a/timing @icon-bg-animation-ref common-timing)
                                                                      (a/timing @title-animation-ref common-timing)
                                                                      (a/timing @subtitle-animation-ref common-timing)
                                                                      (a/timing @icon-animation-ref common-timing)])]))
        device (get-running-device)
        os-version (oget Device :osVersion)
        provider-link (get-provider-link props)
        on-press (if (and (= device "ios") (ios-version>13? os-version))
                   #(ocall WebBrowser :openBrowserAsync provider-link)
                   #(navigate navigation (:screen item)))]

    (hooks/use-effect :once (-> @on-render-animate
                                (a/start #(ocall @lottie-ref :play))))

    ($ View {:style [{:height item-height}
                     (tw :w-full :relative :m-2 :py-4 :items-center :justify-center)]}
       ($ a/View {:style [{:opacity @border-animation-ref
                           :height item-height
                           :right (a/interpolate @border-animation-ref {:output-range [50 0]})}
                          (tw :absolute :top-0 :bottom-0 :left-0 :border-gray-light-2 :border-2 :rounded-3xl)]})
       ($ TouchableOpacity {:onPress on-press
                            :style [{:height item-height} (tw :flex-1 :items-center :justify-center :flex-row)]}
          ($ a/View {:style [{:flex 0.3}]}
             ($ a/View {:style [{:height 60
                                 :opacity @icon-bg-animation-ref
                                 :width (a/interpolate @icon-bg-animation-ref {:output-range [0 60]})}
                                (tw (:bg-color item) :rounded-lg :m-4)]})
             ($ a/View {:style [{:opacity @icon-animation-ref
                                 :height 90
                                 :width 90
                                 :left -20}
                                (tw :absolute)]}
                ($ LottieView {:ref (fn [r] (reset! lottie-ref r))
                               :source (:lottie item)
                               :loop false})))
          ($ View {:style [{:flex 0.7}]}
             ($ a/View {:style [{:opacity @title-animation-ref
                                 :left (a/interpolate @title-animation-ref {:output-range [-20 0]})}]}
                ($ text/H4 (:title item)))
             ($ a/View {:style [{:opacity @subtitle-animation-ref
                                 :left (a/interpolate @subtitle-animation-ref {:output-range [-20 0]})}]}
                ($ text/BodyMicro (:description item))))))))

(defnc ProviderList [{:keys [items] :as props}]
  (let [jwt (use-sub props :jwt)
        item-count (count items)
        ItemComponent (if (= item-count 1) SoloProviderItem ProviderItem)]
    ($ View {:style [(tw :items-center :h-full :w-full :mt-10)]}
       (map-indexed (fn [idx item] ($ ItemComponent {:key (:title item)
                                                     :item item :delay (+ 1 (* (inc idx) item-animation-offset))
                                                     :item-height (get-item-height (count items))
                                                     :jwt jwt
                                                     & props})) items))))

(defnc Title [{:keys [current-user animation-ref]}]
  ($ a/View {:style [{:opacity @animation-ref
                      :top (a/interpolate @animation-ref common-top-interpolate-params)}]}
     ($ text/H1 (str "Hi, " (s/capitalize (get current-user :first_name "")) "."))))

(defnc Subtitle [{:keys [animation-ref]}]
  ($ a/View {:style [{:opacity @animation-ref
                      :top (a/interpolate @animation-ref common-top-interpolate-params)}]}
     ($ text/BodyRegular {:style [(tw :mt-4 :text-center)]} "Choose a Digital Health service to get started.")))

(defnc ScreenRenderer [{:keys [navigation] :as props}]
  (let [current-user (use-sub props :current-user)
        provider-ids (providers/get-complete-providers-access (:telemedproviders current-user))
        items (providers/get-providers provider-ids)
        title-animation-ref (hooks/use-ref (a/value 0))
        subtitle-animation-ref (hooks/use-ref (a/value 0))
        profile-picture (use-sub props :profile-picture)
        profile-picture-meta (use-meta-sub props :profile-picture)
        image-uri (or (:uri profile-picture) (:base-64 profile-picture))
        on-render-animate (hooks/use-ref
                           (ocall Animated :stagger 100
                                  #js [(a/timing @title-animation-ref common-timing)
                                       (a/timing @subtitle-animation-ref common-timing)]))]

    (hooks/use-effect :once (-> @on-render-animate
                                (a/start)))

    ($ ScreenContainer
       ($ KeyboardAwareScrollView {:style {:flex 1}}
          ($ View {:style [{:flex 1} (tw :items-end :w-full :h-full)]}
             ($ View {:style [(tw :pt-10 :px-2 :items-center)]}
                ($ TouchableOpacity
                   {:style [(tw :flex-row :items-center)]
                    :onPress #(navigate navigation "account-overview")}
                   ($ text/BodyMicro "Account")
                   ($ View {:style [{:width 32 :height 32} (tw :rounded-full :m-2)]}
                      (if (get-promise profile-picture-meta :load-picture)
                        ($ Loader)
                        (if image-uri
                          ($ Image {:source #js{:uri image-uri}
                                    :style  {:width  "100%" :height "100%" :border-radius 50}})
                          ($ Svg {:type :profile-picture}))))))            
             ($ View {:style [{:flex 0.2} (tw :items-center :justify-center :px-12 :w-full :h-full)]}
                ($ Title {:current-user current-user :animation-ref title-animation-ref})
                ($ Subtitle {:animation-ref subtitle-animation-ref}))
             ($ View {:style [{:flex 0.8} (tw :px-12 :w-full)]}
                ($ ProviderList {:items items & props})))))))

(defnc ScreenWithLoader [props]
  (let [current-user-meta (use-meta-sub props :current-user)
        jwt-meta (use-meta-sub props :jwt)
        jwt (use-sub props :jwt)]
    (if (or (get-promise current-user-meta :load-user)
            (get-promise jwt-meta :load-jwt)
            (nil? jwt))
      ($ Loader)
      ($ ScreenRenderer {& props}))))

(def Screen (with-keechma ScreenWithLoader))
