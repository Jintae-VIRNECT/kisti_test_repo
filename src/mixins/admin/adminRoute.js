export default {
  data() {
    return {}
  },
  computed: {
    menuList() {
      return [
        {
          menu: "groups",
          label: this.$t("group.menu"),
          image: require("assets/image/admin/ic-organization.svg"),
          children: [
            {
              tab: "group",
              label: this.$t("group.menu_management_group"),
              component: () => import("components/admin/groups/AdminGroup")
            },
            {
              tab: "user",
              label: this.$t("group.menu_management_user"),
              component: () => import("components/admin/groups/AdminUser")
            }
          ]
        },
        {
          menu: "message",
          label: this.$t("message.menu"),
          image: require("assets/image/admin/ic-message.svg"),
          children: [
            {
              tab: "whole",
              label: this.$t("message.menu_whole"),
              component: () =>
                import("components/admin/message/AdminMessageAll")
            },
            {
              tab: "notice",
              label: this.$t("message.menu_notice"),
              component: () =>
                import("components/admin/message/AdminMessageNotice")
            }
          ]
        },
        {
          menu: "payment",
          label: this.$t("payment.menu"),
          image: require("assets/image/admin/ic-credit.svg"),
          children: [
            {
              tab: "history",
              label: this.$t("payment.menu_list"),
              component: () =>
                import("components/admin/payment/AdminPaymentHistory")
            },
            {
              tab: "pcontact",
              label: this.$t("payment.menu_contact"),
              component: () =>
                import("components/admin/payment/AdminPaymentContact")
            },
            {
              tab: "coupon",
              label: this.$t("payment.menu_coupon"),
              component: () =>
                import("components/admin/payment/AdminExperienceCode")
            }
          ]
        },
        {
          menu: "help",
          label: this.$t("help.menu"),
          image: require("assets/image/admin/ic-faq.svg"),
          children: [
            {
              tab: "faq",
              label: this.$t("help.menu_faq"),
              component: () => import("components/admin/help/AdminHelpFaq")
            },
            {
              tab: "contact",
              label: this.$t("help.menu_contact"),
              component: () => import("components/admin/help/AdminHelpContact")
            }
          ]
        },
        {
          menu: "setting",
          label: this.$t("setting.menu"),
          image: require("assets/image/admin/ic-settings.svg"),
          children: [
            {
              tab: "account",
              label: this.$t("setting.menu_account"),
              component: () => import("components/admin/setting/AdminSetting")
            },
            {
              tab: "policy",
              label: this.$t("setting.menu_policy"),
              click: () => {
                window.open("/policy")
              }
            },
            {
              tab: "language",
              label: this.$t("setting.menu_language"),
              component: () => import("components/admin/setting/AdminLanguage")
            }
          ]
        }
      ]
    },
    mx_currentMenu() {
      return this.$route.params.menu
    },
    mx_currentTab() {
      return this.$route.params.tab
    }
  },
  methods: {
    mx_changePage(menuName, tabName) {
      let url = `/admin`
      let menu
      let tab

      if (this.mx_currentMenu || this.mx_currentTab) {
        if (
          this.mx_currentMenu === menuName &&
          this.mx_currentTab === tabName
        ) {
          if (this.$route.query && !this.$route.query.type) return
          this.$eventBus.$emit("setComp")
          return
        }
      }

      //1차. 메뉴 검증
      //parameter 검증
      if (menuName && this.menuList.find(item => item.menu === menuName)) {
        menu = this.menuList.find(item => item.menu === menuName)
        //현재 메뉴 검증
      } else if (
        true === !!this.menuList.find(item => item.menu === this.mx_currentMenu)
      ) {
        menu = this.menuList.find(item => item.menu === this.mx_currentMenu)
        //default
      } else {
        menu = this.menuList[0]
      }
      url += `/${menu.menu}`

      //2차. 탭 검증
      //parameter 검증
      if (tabName && menu.children.find(item => item.tab === tabName)) {
        tab = menu.children.find(item => item.tab === tabName)
        //현재 탭 검증
      } else if (
        true === !!menu.children.find(item => item.tab === this.mx_currentTab)
      ) {
        tab = menu.children.find(item => item.tab === this.mx_currentTab)
        //default
      } else {
        tab = menu.children[0]
      }
      url += `/${tab.tab}`

      this.$router.push(url)
    }
  }
}
