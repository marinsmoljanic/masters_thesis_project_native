(ns app.ui.components.inputs
  (:require
   ["react-native" :refer [View TextInput Text Animated TouchableOpacity]]
   ["react-native-check-box" :default CheckBox]
   [keechma.next.helix.core :refer [with-keechma use-meta-sub dispatch]]
   [app.lib :refer [$ defnc convert-style]]
   [helix.dom :as d]
   [helix.hooks :as hooks]
   [oops.core :refer [ocall oget oset!]]
   [keechma.next.controllers.router :as router]
   [app.tailwind :refer [tw]]
   ["react" :as react]
   [app.rn.animated :as animated]
   ["react-dom" :as rdom]
   [app.ui.svgs :refer [Svg]]
   [keechma.next.controllers.form :as form]
   [app.validators :refer [get-validator-message]]))

(defn get-element-props
  [default-props props]
  (let [element-props (into {} (filter (fn [[k v]] (simple-keyword? k)) props))]
    (reduce-kv
     (fn [m k v]
       (let [prev-v (get k m)
             val (cond (and (fn? prev-v) (fn? v))
                       (fn [& args] (apply prev-v args) (apply v args))
                       (and (= :class k) (:class m)) (flatten [v (:class m)])
                       :else v)]
         (assoc m k val)))
     default-props
     element-props)))

;; ERRORS
(defnc ErrorsRenderer [{:keechma.form/keys [controller]
                        :input/keys        [attr]
                        :as                props}]
  (let [errors-getter (hooks/use-callback [attr] #(form/get-errors-in % attr))
        errors (use-meta-sub props controller errors-getter)]
    (when-let [errors' (get-in errors [:$errors$ :failed])]
      ($ View {:style [(tw :text-red :w-full :font-bold :text-sm)]}
         (map-indexed (fn [i e] ($ Text {:key i
                                         :style [(tw :text-red)]} (get-validator-message e)))
                      errors')))))

(def Errors (with-keechma ErrorsRenderer))

;; TEXT
(defnc TextInputRenderer [{:keechma.form/keys [controller]
                           :input/keys        [attr]
                           :as                props}]
  (let [element-props (get-element-props {} props)
        value-getter (hooks/use-callback [attr] #(form/get-data-in % attr))
        value (use-meta-sub props controller value-getter)
        [focused? set-focused] (hooks/use-state false)]
    ($ View {:style (tw :w-full :my-2)}
       (when focused?
         ($ Text {:style [(tw :text-sm :text-gray :pl-4 :pb-2)]}
            (:placeholder element-props)))
       ($ TextInput
          {:value (str value)
           :placeholder (:placeholder element-props)
           :placeholderTextColor "rgba(149, 160, 169, 1)"
           :onFocus #(set-focused true)
           :style [(tw :bg-gray-lighter :px-4 :py-3 :w-full :rounded-18 :text-sm :text-purple)
                   {:font-size 17
                    :line-height 22}]
           :onChangeText #(dispatch props controller :keechma.form.on/change {:value % :attr attr})
           & element-props}))))

(def InputText (with-keechma TextInputRenderer))

;; PASSWORD
(defnc PasswordInputRenderer [{:keechma.form/keys [controller]
                               :input/keys        [attr]
                               :as                props}]
  (let [element-props (get-element-props {} props)
        icon-end (:icon-end props)
        value-getter (hooks/use-callback [attr] #(form/get-data-in % attr))
        value (use-meta-sub props controller value-getter)
        [password? set-password] (hooks/use-state true)
        [focused? set-focused] (hooks/use-state false)]
    ($ View {:style (tw :w-full :my-2)}
       (when focused?
         ($ Text {:style [(tw :text-sm :text-gray :pl-4 :pb-2)]}
            (:placeholder element-props)))
       ($ View
          (if icon-end
            ($ View {:style [(tw :absolute :right-0 :w-8 :mr-2 :items-center :top-0 :bottom-0 :z-50)]}
             ($ Svg {:type icon-end}))
            ($ TouchableOpacity {:style [(tw :absolute :right-0 :w-8 :mr-2 :items-center :top-0 :bottom-0 :z-50)]
                               :onPress #(set-password (not password?))}
             ($ Svg {:type (if password? :eye-slash :eye)})))
          ($ TextInput
             {:value (str value)
              :secureTextEntry password?
              :placeholder (:placeholder element-props)
              :placeholderTextColor "rgba(149, 160, 169, 1)"
              :onFocus #(set-focused true)
              :style [(tw :bg-gray-lighter :px-4 :py-3 :w-full :rounded-18 :text-sm :text-purple)
                      {:font-size 17
                       :line-height 22}]
              :onChangeText #(dispatch props controller :keechma.form.on/change {:value % :attr attr})
              & element-props})))))

(def InputPassword (with-keechma PasswordInputRenderer))


;; CHECKBOX


(defnc CheckboxRenderer [{:keechma.form/keys [controller]
                          :input/keys        [attr]
                          :as                props}]
  (let [element-props (get-element-props {} props)
        value-getter (hooks/use-callback [attr] #(form/get-data-in % attr))
        value' (use-meta-sub props controller value-getter)
        value (if (nil? value') false value')]
    ($ View {:style [(tw :flex :flex-1 :flex-row :justify-center :items-center :text-gray)]}
       ($ CheckBox
          {:isChecked      value
           :rightText      (:label props)
           :checkBoxColor  "#66737C"
           :onClick        (fn [e]
                             (dispatch props
                                       controller
                                       :keechma.form.on/change
                                       {:value (not value) :attr attr}))
           &               element-props})
       ($ View {:style [(tw :flex :flex-row :justify-center :items-center)]}
          ($ TouchableOpacity {:onPress (fn [e]
                                          (dispatch props
                                                    controller
                                                    :keechma.form.on/change
                                                    {:value (not value) :attr attr}))}
             ($ Text "I agree to the "))
          ($ TouchableOpacity {:onPress #(dispatch props :router :redirect "legal")}
             ($ Text {:style [(tw :underline)]} "Terms & Conditions"))))))

(def Checkbox (with-keechma CheckboxRenderer))

(defmulti input (fn [props] (:input/type props)))
(defmethod input :text [props] ($ InputText {& props}))
(defmethod input :password [props] ($ InputPassword {& props}))
(defmethod input :checkbox [props] ($ Checkbox {& props}))

(defmulti wrapped-input (fn [props] (:input/type props)))
(defmethod wrapped-input :default [props] (input props))

(def AnimatedView (oget Animated :View))

(defmethod wrapped-input :text [props]
  ($ AnimatedView {:style [(tw :w-full)
                           {:animation/top (:top props)
                            :animation/opacity (:opacity props)}]}
     (input props)
     ($ Errors {& props})))

(defmethod wrapped-input :password [props]
  ($ AnimatedView {:style [(tw :w-full)
                           {:animation/top (:top props)
                            :animation/opacity (:opacity props)}]}
     (input props)
     ($ Errors {& props})))

(defmethod wrapped-input :checkbox [props]
  ($ AnimatedView {:style [(tw :w-full)
                           {:animation/top (:top props)
                            :animation/opacity (:opacity props)}]}
     (input props)
     ($ Errors {& props})))