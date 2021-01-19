(ns app.ui.screens.person-edit
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
            [app.rn.animated :as a]
            [app.ui.components.shared :refer [MarginV ButtonBigGray Errors]]))

(def AnimatedView (oget Animated :View))

(def duration 600)

(defnc PageContainer [{:keys [children]}]
       ($ View {:style [(tw :h-full :w-full :flex :flex-1 :px-4 :pb-10 :mt-2)]} children))

(defnc FormContainer [{:keys [children]}]
       ($ View {:style [(tw :h-full :w-full :flex :flex-1 :px-4 :mt-2)]} children))

(defnc TableHeader [_]
       ($ View {:style [(tw :flex :flex-row :bg-purple :mt-4)]}
          ($ View {:style [(tw :flex :items-center :justify-center :pt-2 :pb-2 :border-r-2 :border-white :border-solid) {:width "33.33333%"}]}
             ($ Text {:style [(tw :text-white :px-2 :text-center)
                              {:font-size 17
                               :line-height 22}]} "Projekt"))
          ($ View {:style [(tw :flex :items-center :justify-center :pt-2 :pb-2 ) {:width "33.33333%"}]}
             ($ Text {:style [(tw :text-white)
                              {:font-size 17
                               :line-height 22}]} "Uloga"))
          ($ View {:style [(tw :flex :items-center :justify-center :pt-2 :pb-2 :border-l-2 :border-white :border-solid) {:width "33.33333%"}]}
             ($ Text {:style [(tw :text-white)
                              {:font-size 17
                               :line-height 22}]} "Datum dodjele"))))

(defnc ClickableTableItem [{:keys [roleName projectName assignmentDate navigation]}]
       ($ TouchableOpacity
          {:onPress #(navigate navigation "person-role-by-person" #js{:firstName "Testno ime"})
           :activeOpacity 0.9
           :style (tw :flex :flex-row :bg-white :w-full :border-b :border-l :border-r :border-solid :border-gray-light)}
          ($ View {:style [(tw :flex :items-start :justify-center :pt-2 :pb-2
                               :border-l :border-r :border-b :border-gray-light :border-solid) {:width "33.33333%"}]}
             ($ Text {:style [(tw :text-black :px-2)
                              {:font-size 17
                               :line-height 22}]} projectName))
          ($ View {:style [(tw :flex :items-start :justify-center :pt-2 :pb-2
                               :border-b :border-gray-light :border-solid) {:width "33.33333%"}]}
             ($ Text {:style [(tw :text-black :px-2)
                              {:font-size 17
                               :line-height 22}]} roleName))
          ($ View {:style [(tw :flex :items-start :justify-center :pt-2 :pb-2
                               :border-l :border-r :border-b :border-gray-light :border-solid) {:width "33.33333%"}]}
             ($ Text {:style [(tw :text-black :px-2)
                              {:font-size 17
                               :line-height 22}]} assignmentDate))))

(defnc EditPersonForm [props]
            ($ FormContainer
               (wrapped-input {:keechma.form/controller :person-edit-form
                               :input/type              :text
                               :input/attr              :firstName
                               :placeholder             "Ime"
                               :autoCapitalize          "none"})

               (wrapped-input {:keechma.form/controller :person-edit-form
                               :input/type              :text
                               :input/attr              :lastName
                               :placeholder             "Prezime"
                               :autoCapitalize          "none"})

               ($ View {:style [(tw "flex flex-row w-full items-center justify-center mt-8 border-solid pb-8 border-b border-gray-light")]}
                  ($ buttons/Medium {:onPress #(dispatch props :person-edit-form :delete-person)
                                  :title    "Obriši"
                                  :style    [(tw :mr-2)]
                                  :text-style [(tw :text-white)]})
                  ($ buttons/Medium {:onPress #(dispatch props :person-edit-form :keechma.form/submit)
                                     :title    "Spremi"
                                     :style    [(tw :ml-2)]
                                     :text-style [(tw :text-white)]}))))

(defnc ScreenRenderer   [{:keys [route] :as props}]
       (let [navigation (useNavigation)
             [visible set-visible] (hooks/use-state false)
             image-uri (:uri (use-sub props :image-upload))
             image-upload-top-value (hooks/use-ref (animated/value 500))
             subtitle-top-value (hooks/use-ref (animated/value 500))
             animate-image-upload (hooks/use-ref (animated/timing @image-upload-top-value {:duration duration :to-value 0}))
             animate-subtitle (hooks/use-ref (animated/timing @subtitle-top-value {:duration duration :to-value 0}))
             first-name (oget route :params.firstName)
             last-name  (oget route :params.lastName)
             person-id  (oget route :params.id)

             person-roles (use-sub props :person-role-by-personid)]

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
                     ($ PageContainer
                        ($ AnimatedView {:style [(tw :relative)
                                                 {:top @subtitle-top-value
                                                  :opacity (animated/interpolate @subtitle-top-value {:input-range [0 25 50] :output-range [1 0.5 0]})}]}
                           ($ EditPersonForm {:first-name first-name
                                              :last-name  last-name
                                              :person-id  person-id
                                              &           props})

                           ($ View {:style [(tw "flex flex-row justify-between w-full pt-8")]}
                              ($ Text {:style [(tw :text-gray-light :px-2 :text-center)
                                               {:font-size 14
                                                :line-height 22}]} "Zaduženja osobe")

                              ($ buttons/Zaduzenje {:onPress #(navigate navigation "person-role")
                                                 :title    "+ Dodaj"
                                                 :style    [(tw :bg-purple)]
                                                 :text-style [(tw :text-white)]}))
                           ($ TableHeader)
                           (map (fn [person-role]
                                    ($ ClickableTableItem {:roleName       (:roleName person-role)
                                                           :projectName    (:projectName person-role)
                                                           :roleId         (:RoleId person-role)
                                                           :projectId      (:ProjectCode person-role)
                                                           :assignmentDate (:AssignmentDate person-role)

                                                           ;; :person-name    person-name
                                                           ;; :person-surname person-surname
                                                           ;; :person-id      person-id

                                                           :key            (:id person-role)
                                                           :id             (:id person-role)
                                                           &               props}))
                                person-roles))))))))

(def Screen (with-keechma ScreenRenderer))