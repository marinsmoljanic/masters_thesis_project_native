(ns app.ui.screens.role
  (:require ["react-native" :refer [View Image Text TextInput ScrollView TouchableOpacity Animated]]
            ["react-native-keyboard-aware-scroll-view" :refer [KeyboardAwareScrollView]]
            ["lottie-react-native" :as LottieView]
            [app.lib :refer [$ defnc]]
            [helix.hooks :as hooks]
            [app.ui.components.buttons :as buttons]
            [app.tailwind :refer [tw]]
            [app.ui.components.inputs :refer [wrapped-input]]
            [keechma.next.helix.core :refer [with-keechma dispatch use-meta-sub use-sub]]
            [app.ui.components.screen-container :refer [ScreenContainer]]
            [app.ui.components.text :refer [Subtitle FormTitle]]
            [keechma.next.controllers.form :as form]
            ["react-native-modal" :default Modal]
            [app.rn.animated :as animated]
            [app.rn.navigation :refer [navigate]]
            [oops.core :refer [oget ocall]]
            ["@react-navigation/native" :refer [useNavigation]]
            [app.ui.svgs :refer [Svg]]
            ["expo-av" :refer [Video]]
            ["expo-linking" :as Linking]
            [app.util :refer [resolve-error]]
            [app.ui.components.shared :refer [MarginV ButtonBigGray Errors]]))

(def AnimatedView (oget Animated :View))

(def duration 600)

(defnc RegisterPageContainer [{:keys [children]}]
       ($ View {:style [(tw :h-full :w-full :flex :flex-1 :px-4 :pb-10)]} children))

(defnc SingleFormContainer [{:keys [children]}]
       ($ View {:style [(tw :w-full :flex :flex-col :px-4 :mt-4)]} children))

(defnc RoleEditForm [{:keys [form-id] :as props}]
       (let [form-ident [:role-edit-form form-id]]
            ($ SingleFormContainer
               (wrapped-input {:keechma.form/controller form-ident
                               :input/type              :text
                               :input/attr              :Name
                               :placeholder             "Naziv uloge"
                               :autoCapitalize          "none"})

               ($ View {:style [(tw "w-full flex flex-row items-center justify-end")]}
                  ($ buttons/SmallDelete {:onPress    #(dispatch props form-ident :delete form-id)
                                    :title      "Obrisi"
                                    :style      [(tw :bg-red)]
                                    :text-style [(tw :text-white)]})

                  ($ buttons/Small {:onPress    #(dispatch props form-ident :keechma.form/submit)
                                    :title      "Spremi"
                                    :style      [(tw :bg-purple)]
                                    :text-style [(tw :text-white)]})))))

(defnc RoleCreateForm [{:keys [form-id] :as props}]
       (let [form-ident [:role-edit-form form-id]]
            ($ SingleFormContainer
               (wrapped-input {:keechma.form/controller form-ident
                               :input/type              :text
                               :input/attr              :Name
                               :placeholder             "Naziv uloge"
                               :autoCapitalize          "none"})

               ($ View {:style [(tw "w-full flex flex-row items-center justify-end")]}
                  ($ buttons/SmallDelete {:onPress    #(dispatch props form-ident :delete form-id)
                                          :title      "Obrisi"
                                          :style      [(tw :bg-red)]
                                          :text-style [(tw :text-white)]})

                  ($ buttons/Small {:onPress    #(dispatch props form-ident :keechma.form/submit)
                                    :title      "Spremi"
                                    :style      [(tw :bg-purple)]
                                    :text-style [(tw :text-white)]})))))

(defnc ScreenRenderer [props]
       (let [navigation (useNavigation)
             [visible set-visible] (hooks/use-state false)
             image-uri (:uri (use-sub props :image-upload))
             image-upload-top-value (hooks/use-ref (animated/value 500))
             subtitle-top-value (hooks/use-ref (animated/value 500))
             animate-image-upload (hooks/use-ref (animated/timing @image-upload-top-value {:duration duration :to-value 0}))
             animate-subtitle (hooks/use-ref (animated/timing @subtitle-top-value {:duration duration :to-value 0}))
             roles (use-sub props :roles)]
            (hooks/use-effect :once
                              (-> (ocall Animated :stagger 200
                                         #js[@animate-image-upload
                                             @animate-subtitle])
                                  (animated/start)))

            ($ ScreenContainer
               ($ ScrollView {:scrollIndicatorInsets #js{:right 1}}
                  ($ KeyboardAwareScrollView
                     {:style [(tw :flex :flex-1)]
                      :enableOnAndroid true
                      :extraScrollHeight 75
                      :extraHeight 75
                      :keyboardShouldPersistTaps "always"}
                     ($ RegisterPageContainer
                        ($ AnimatedView {:style [(tw :relative)
                                                 {:top @subtitle-top-value
                                                  :opacity (animated/interpolate @subtitle-top-value {:input-range [0 25 50] :output-range [1 0.5 0]})}]}
                           ($ View {:style [(tw :flex :items-center :justify-center :mt-5 :mb-6)]}
                              (map (fn [{:keys [id]}]
                                       ($ RoleEditForm {:form-id id
                                                        :key     id
                                                        &        props}))
                                   roles))


                           ($ SingleFormContainer
                              (wrapped-input {:keechma.form/controller :role-form
                                              :input/type              :text
                                              :input/attr              :Name
                                              :placeholder             "Naziv nove uloge"
                                              :autoCapitalize          "none"})

                              ($ View {:style [(tw "w-full flex flex-row items-center justify-end")]}
                                 ($ buttons/Small {:onPress    #(dispatch props :role-form :keechma.form/submit)
                                                   :title      "Dodaj"
                                                   :text-style [(tw :text-white)]})))
                           )))))))

(def Screen (with-keechma ScreenRenderer))
