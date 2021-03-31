<template>
  <div class="workspace-select">
    <div
      class="workspace-select__value"
      :class="{ focus: isShow }"
      @click="toggleShow"
    >
      <img
        :src="workspace.profile"
        @error="({ target }) => (target.src = defaultProfile)"
      />
      <span class="workspace-select__name">{{ workspace.name }}</span>
    </div>
    <div v-if="isShow" class="workspace-select__dropdown">
      <div>
        <div
          v-for="category in ['master', 'manager', 'member']"
          class="workspace-select__dropdown__group"
          :key="category"
        >
          <div class="workspace-select__dropdown__category">
            <span>{{ $t(`workspace-select.${category}`) }}</span>
          </div>
          <div v-if="!workspaceList.filter(roleFilter(category)).length">
            <span>-</span>
          </div>
          <div
            class="workspace-select__dropdown__value"
            v-for="w in workspaceList.filter(roleFilter(category))"
            :key="w.uuid"
            @click="handleChange(w)"
          >
            <img
              :src="w.profile"
              @error="({ target }) => (target.src = defaultProfile)"
            />
            <span class="workspace-select__name">{{ w.name }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue-demi'

export default {
  props: {
    activeWorkspace: Object,
    workspaceList: Array,
  },
  emits: ['onChange'],
  setup(props, { root, emit }) {
    // dropdown ui
    const isShow = ref(false)
    const toggleShow = () => (isShow.value = !isShow.value)
    let bodyClickListener = null
    onMounted(() => {
      bodyClickListener = document.addEventListener('click', event => {
        if (!event.target.closest('.workspace-select')) isShow.value = false
      })
    })
    onUnmounted(() => {
      document.removeEventListener('click', bodyClickListener)
    })

    // workspace model
    const workspace = ref(props.activeWorkspace)
    const handleChange = w => {
      workspace.value = w
      toggleShow()
      emit('onChange', w)
    }
    const roleFilter = category => w => w.role.toLowerCase() === category

    // lang
    root.$i18n.mergeLocaleMessage('ko', {
      'workspace-select': {
        master: '워크스페이스 마스터',
        manager: '워크스페이스 매니저',
        member: '워크스페이스 멤버',
      },
    })
    root.$i18n.mergeLocaleMessage('en', {
      'workspace-select': {
        master: 'Master',
        manager: 'Manager',
        member: 'Member',
      },
    })

    return {
      isShow,
      toggleShow,
      workspace,
      handleChange,
      roleFilter,
      defaultProfile: root.$defaultWorkspaceProfile,
    }
  },
}
</script>

<style lang="scss" scoped>
$color-bg: #f0f3f8;
$color-focus: #5696f0;
$color-category: #566173;
$svg-mask: url('~assets/images/icon/ic-bg.svg');
$svg-more: url('~assets/images/icon/ic-expand-more.svg');
$width: 224px;

.workspace-select {
  position: relative;
  text-align: left;

  ::-webkit-scrollbar {
    width: 8px;
    height: 8px;
  }
  ::-webkit-scrollbar-track {
    background: transparent;
  }
  ::-webkit-scrollbar-thumb {
    background: rgba(74, 92, 129, 0.2);
    border-radius: 5px;
  }

  img {
    width: 22px;
    height: 22px;
    margin-right: 8px;
    object-fit: cover;
    mask: $svg-mask;
    mask-size: 100%;
  }
  span {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }

  &__value {
    display: flex;
    align-items: center;
    box-sizing: border-box;
    width: $width;
    padding: 5px 10px;
    line-height: 22px;
    background: $color-bg;
    border: 2px solid $color-bg;
    border-radius: 6px;
    cursor: pointer;
    transition: 0.2s;

    & > span {
      flex-grow: 1;
    }
    &:after {
      flex-shrink: 0;
      width: 20px;
      height: 20px;
      background: $svg-more;
      content: '';
    }
    &.focus {
      background: #fff;
      border-color: $color-focus;
      &:after {
        transform: rotateX(180deg);
      }
    }
  }
  &__dropdown {
    position: absolute;
    top: 40px;
    left: 0;
    width: $width;
    background-color: #fff;
    border: solid 1px #e6e9ee;
    border-radius: 3px;

    & > div {
      max-height: 350px;
      margin: 6px;
      overflow-y: auto;
    }

    &__group:not(:last-child) {
      margin-bottom: 8px;
    }

    &__category {
      margin-bottom: 4px;
      color: $color-category;
      font-size: 12px;
      line-height: 18px;
    }

    span {
      padding-left: 6px;
    }

    &__value {
      display: flex;
      padding: 7px 6px;
      line-height: 22px;
      cursor: pointer;
      > span {
        padding-left: 0;
      }
      &:hover {
        background: $color-bg;
      }
    }
  }
}

.dark .workspace-select {
  &__value {
    background: var(--color-gray-30);
    &:not(.focus) {
      border-color: var(--color-gray-30);
    }
  }
  &__dropdown {
    background: var(--color-gray-30);
    border-color: var(--color-gray-50);
    &__category {
      color: var(--color-gray-90);
    }
    &__value:hover {
      background: var(--color-gray-40);
    }
  }
}
</style>
