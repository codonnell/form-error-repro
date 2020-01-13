(ns form-error-repro.core
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as str]

   [com.fulcrologic.fulcro.algorithms.form-state :as fs]
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as m]

   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form :refer [ui-form]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-input :refer [ui-form-input]]
   ))

(defonce SPA (app/fulcro-app {}))

(s/def :user/name (s/and string? (complement str/blank?)))

(defsc MyForm [this {:user/keys [id name] :as user}]
  {:query [:user/id :user/name fs/form-config-join]
   :ident :user/id
   :initial-state (fn [_]
                    (fs/add-form-config
                      MyForm
                      {:user/id 1
                       :user/name ""}))
   :form-fields #{:user/name}}
  (ui-form {:onSubmit #(js/console.log "Submit")}
    ;; This version works
    (ui-form-input (cond-> {:onChange (fn [evt]
                                        (m/set-string! this :user/name :event evt)
                                        (comp/transact! this [(fs/mark-complete! {:field :user/name})]))
                            :error (and (fs/invalid-spec? user :user/name) "User name cannot be blank")
                            :fluid true
                            :value name}))
    ;; This version does not work
    ;; (ui-form-input (cond-> {:onChange (fn [evt]
    ;;                                     (m/set-string! this :user/name :event evt)
    ;;                                     (comp/transact! this [(fs/mark-complete! {:field :user/name})]))
    ;;                         :fluid true
    ;;                         :value name}
    ;;                  (fs/invalid-spec? user :user/name)
    ;;                  (assoc :error "User name cannot be blank")))
    (ui-button {:type "submit"} "Submit")))

(def ui-my-form (comp/factory MyForm))

(defsc Root [this {:root/keys [my-form] :as props}]
  {:query [{:root/my-form (comp/get-query MyForm)}]
   :initial-state {:root/my-form {}}}
  (ui-my-form my-form))

(defn ^:export refresh []
  (app/mount! SPA Root "app"))

(defn ^:export init []
  (app/mount! SPA Root "app"))
