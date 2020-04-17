<template>
  <div class="test">
    <h1 class="test-title">Components</h1>

    <section class="test-section" style="background-color: #1e1e20;">
      <h2 class="subtitle">Modal Text</h2>
      <div class="action-box">
        <div class="component">
          <button @click="modalTest = true" style="color: #fff;">
            원격 협업 생성하기
          </button>
          <create-room :visible.sync="modalTest"></create-room>
        </div>
      </div>
    </section>

    <section class="test-section" style="background-color: #1e1e20;">
      <h2 class="subtitle">[개발 완]Button 컴포넌트</h2>
      <div class="action-box">
        <div class="component">
          <div style="width: 300px;">
            <button class="btn">원격 협업 생성</button>
            <button class="btn small">재시작</button>
            <button class="btn large">시작하기</button>
            <icon-button
              :imgSrc="require('assets/image/back/mdpi_tr.svg')"
              :text="'삭제하기'"
            ></icon-button>
            <icon-button
              :imgSrc="require('assets/image/back/mdpi_icn_renew.svg')"
              :text="'새로고침'"
            ></icon-button>
            <button class="btn normal">협업 프로필 등록</button>
            <button class="btn line">내보내기</button>
          </div>
        </div>
      </div>
    </section>

    <section class="test-section" style="background-color: #1e1e20;">
      <h2 class="subtitle">[개발 완]Input 컴포넌트</h2>
      <div class="action-box">
        <div class="component">
          <div style="width: 300px;">
            <input-row
              :title="inputOption.title"
              :placeholder="inputOption.placeholder"
              :value.sync="inputOption.value1"
              type="text"
              :count="20"
            ></input-row>
            <input-row
              :title="inputOption.title"
              :placeholder="inputOption.placeholder"
              :value.sync="inputOption.value1"
              type="textarea"
              :count="50"
            ></input-row>
          </div>
        </div>
        <div class="props">
          <div class="props-option">
            <p class="props-title">title</p>
            <input
              class="props-options"
              type="text"
              v-model="inputOption.title"
            />
          </div>
          <div class="props-option">
            <p class="props-title">placeholder</p>
            <input
              class="props-options"
              type="text"
              v-model="inputOption.placeholder"
            />
          </div>
          <div class="props-option">
            <p class="props-title">value1</p>
            <input
              class="props-options"
              type="text"
              disabled
              v-model="inputOption.value1"
            />
          </div>
          <div class="props-option">
            <p class="props-title">value2</p>
            <input
              class="props-options"
              type="text"
              disabled
              v-model="inputOption.value2"
            />
          </div>
        </div>
      </div>
    </section>

    <section class="test-section select" style="background-color: #1e1e20;">
      <h2 class="subtitle">Modal</h2>
      <div class="action-box">
        <div class="component">
          <button
            @click="modalOption.visible = !modalOption.visible"
            style="color: #fff;"
          >
            모달 테스트
          </button>
          <modal
            :visible.sync="modalOption.visible"
            :title="'원격협업 상세보기'"
            :showClose="true"
            :width="898"
          >
            <div style="height: 300px;"></div>
          </modal>
        </div>
        <!-- <div class="props">
          <div class="props-option">
            <p class="props-title">value</p>
            <input
              class="props-options"
              type="text"
              disabled
            />
          </div>
        </div> -->
      </div>
    </section>

    <section class="test-section" style="background-color: #1e1e20;">
      <h2 class="subtitle">Card</h2>
      <div class="action-box">
        <div class="component">
          <card></card>
          <br />
          <wide-card>
            <profile
              :image="require('assets/image/img_user_profile.svg')"
              imageAlt="버넥트 리모트01"
              mainText="버넥트 리모트01"
              subText="example@example.com"
              status="online"
              role="Master"
            ></profile>
          </wide-card>
        </div>
      </div>
    </section>

    <section class="test-section select" style="background-color: #1e1e20;">
      <h2 class="subtitle">[개발 완]Radio</h2>
      <div class="action-box">
        <div class="component">
          <r-radio
            :options="radioOption.options"
            :text="radioOption.text"
            :value="radioOption.value"
            :selectedOption.sync="radioOption.selectedOption"
          ></r-radio>
        </div>
        <div class="props">
          <div class="props-option">
            <p class="props-title">value</p>
            <input
              class="props-options"
              type="text"
              disabled
              v-model="radioOption.selectedOption"
            />
          </div>
        </div>
      </div>
    </section>

    <section class="test-section select" style="background-color: #1e1e20;">
      <h2 class="subtitle">[디자인 필요]Switch</h2>
      <div class="action-box">
        <div class="component">
          <switcher :value.sync="switchOption.value"></switcher>
        </div>
        <div class="props">
          <div class="props-option">
            <p class="props-title">value</p>
            <input
              class="props-options"
              type="text"
              disabled
              v-model="switchOption.value"
            />
          </div>
        </div>
      </div>
    </section>

    <section class="test-section select" style="background-color: #29292c;">
      <h2 class="subtitle">[개발 완]Progress</h2>
      <div class="action-box">
        <div class="component">
          <progress-bar
            :value="progress.value"
            :max="progress.max"
          ></progress-bar>
        </div>
        <div class="props">
          <div class="props-option">
            <p class="props-title">max</p>
            <input class="props-options" type="number" v-model="progress.max" />
          </div>
          <div class="props-option">
            <p class="props-title">description</p>
            <input
              class="props-options"
              type="number"
              v-model="progress.value"
            />
          </div>
        </div>
      </div>
    </section>

    <section class="test-section select" style="background-color: #1e1e20;">
      <h2 class="subtitle">[개발 완]Profile</h2>
      <div class="action-box">
        <div class="component">
          <div>
            <profile
              :image="require('assets/image/img-default-user.svg')"
              imageAlt="버넥트 리모트01"
              mainText="버넥트 리모트01"
              subText="example@example.com"
              status="online"
            ></profile>
          </div>
          <br />
          <div>
            <profile
              color="#267bff"
              mainText="버넥트"
              subText="버넥트 솔루션 사업부/Remote"
            ></profile>
          </div>
          <br />

          <profile-image :btnText="'등록'"></profile-image>
        </div>
        <!-- <div class="props">
          <div class="props-option">
            <p class="props-title">description</p>
            <input
              class="props-options"
              type="text"
              v-model="toogleButton.description"
            />
          </div>
        </div> -->
      </div>
    </section>

    <section class="test-section select" style="background-color: #1e1e20;">
      <h2 class="subtitle">[개발 완]Select</h2>
      <div class="action-box">
        <div class="component">
          <r-select
            :options="selectOptions.options"
            :value="selectOptions.value"
            :text="selectOptions.text"
            v-on:changeValue="selectOptionValue"
          ></r-select>
        </div>
        <div class="props">
          <div class="props-option">
            <p class="props-title">selection value</p>
            <input
              class="props-options"
              type="text"
              v-model="selectOptions.selection"
            />
          </div>
        </div>
      </div>
    </section>

    <section class="test-section" style="background-color: #1e1e20;">
      <h2 class="subtitle">[개발 완]Scrollbar</h2>
      <div class="action-box">
        <div class="component" style="position: relative;height: 100px;">
          <scroller>
            <div style="height: 400px;border: solid 1px #d8d8d8;"></div>
          </scroller>
        </div>
      </div>
    </section>

    <section class="test-section" style="background-color: #1e1e20;">
      <h2 class="subtitle">[개발 완]Search</h2>
      <div class="action-box">
        <div class="component">
          <search
            :placeholder="search.placeholder"
            v-on:search="txt => (search.value = txt)"
          ></search>
        </div>
        <div class="props">
          <div class="props-option">
            <p class="props-title">placeholder</p>
            <input
              class="props-options"
              type="text"
              v-model="search.placeholder"
            />
          </div>
          <div class="props-option">
            <p class="props-title">Search Value</p>
            <span style="color: #fff;">{{ search.value }}</span>
          </div>
        </div>
      </div>
    </section>

    <section class="test-section">
      <h2 class="subtitle">[개발 완]Toggle Button</h2>
      <div class="action-box">
        <div class="component">
          <toggle-button
            :description="toogleButton.description"
            :size="toogleButton.size"
            :active="toggle"
            :activeSrc="require('assets/image/call/gnb_ic_voice_on.svg')"
            :inactiveSrc="require('assets/image/call/gnb_ic_voice_off.svg')"
            @action="toggle = !toggle"
          ></toggle-button>
        </div>
        <div class="props">
          <div class="props-option">
            <p class="props-title">description</p>
            <input
              class="props-options"
              type="text"
              v-model="toogleButton.description"
            />
          </div>
          <div class="props-option">
            <p class="props-title">size</p>
            <input
              class="props-options"
              type="number"
              v-model="toogleButton.size"
            />
          </div>
          <div class="props-option">
            <p class="props-title">activeSrc</p>
            <input
              class="props-options"
              type="text"
              value="require('assets/image/call/gnb_ic_voice_on.svg')"
              disabled
            />
          </div>
          <div class="props-option">
            <p class="props-title">inactiveSrc</p>
            <input
              class="props-options"
              type="text"
              value="require('assets/image/call/gnb_ic_voice_off.svg')"
              disabled
            />
          </div>
          <div class="props-option">
            <p class="props-title">@action</p>
            <input
              class="props-options"
              type="text"
              value="click event"
              disabled
            />
          </div>
        </div>
      </div>
    </section>

    <section class="test-section">
      <h2 class="subtitle">Popover</h2>
      <div class="action-box">
        <div class="component">
          <popover :placement="popover.position" :trigger="popover.trigger">
            <button slot="reference">popover</button>
          </popover>
        </div>
        <div class="props">
          <div class="props-option">
            <p class="props-title">placement</p>

            <select class="props-options" v-model="popover.position">
              <option
                v-for="(option, idx) in popover.positionOptions"
                :key="idx"
                :value="option"
                >{{ option }}</option
              >
            </select>
          </div>
          <div class="props-option">
            <p class="props-title">trigger</p>

            <select class="props-options" v-model="popover.trigger">
              <option
                v-for="(option, idx) in popover.triggerOptions"
                :key="idx"
                :value="option"
                >{{ option }}</option
              >
            </select>
          </div>
        </div>
      </div>
    </section>
    <section class="test-section">
      <h2 class="subtitle">[개발 완]Tooltip</h2>
      <div class="action-box">
        <div class="component">
          <tooltip :content="tooltip.content" :placement="tooltip.placement">
            <button slot="body" @click="toogleOnOff">툴팁</button>
          </tooltip>
        </div>
        <div class="props">
          <div class="props-option">
            <p class="props-title">content</p>
            <input class="props-options" v-model="tooltip.content" />
          </div>
          <div class="props-option">
            <p class="props-title">placement</p>

            <select class="props-options" v-model="tooltip.placement">
              <option
                v-for="(option, idx) in tooltip.placementOptions"
                :key="idx"
                :value="option"
                >{{ option }}</option
              >
            </select>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
<script>
import CreateRoom from 'components/workspace/modal/WorkspaceCreateRoom'
import ProfileImage from 'ProfileImage'
import InputRow from 'InputRow'
import RRadio from 'RemoteRadio'
import Switcher from 'Switcher'
import ProgressBar from 'ProgressBar'
import Scroller from 'Scroller'
import WideCard from 'components/workspace/modules/WideCard'
import Card from 'components/workspace/modules/Card'
import Profile from 'Profile'
// import CardView from 'CardView'
// import ListView from 'ListView'
// import DataView from 'DataView'
import RSelect from 'RemoteSelect'
import Popover from 'Popover'
import Tooltip from 'Tooltip'
import Modal from 'Modal'
import ToggleButton from 'ToggleButton'
import Search from 'components/workspace/modules/Search'
import IconButton from 'components/workspace/modules/IconButton'
export default {
  components: {
    CreateRoom,
    ProfileImage,
    InputRow,
    RRadio,
    Switcher,
    ProgressBar,
    Scroller,
    WideCard,
    Card,
    Profile,
    // CardView,
    // ListView,
    // DataView,
    RSelect,
    Popover,
    Tooltip,
    Modal,
    ToggleButton,
    Search,
    IconButton,
  },
  data() {
    return {
      modalTest: false,
      inputOption: {
        placeholder: '입력하세열',
        title: '타이틀',
        value1: '',
        value2: '',
      },
      modalOption: {
        visible: false,
      },
      radioOption: {
        options: [
          {
            text: 'English',
            value: 'en',
            imgSrc: require('assets/image/img_flag_USA.svg'),
          },
          {
            text: '한국어',
            value: 'ko',
            imgSrc: require('assets/image/img_flag_korea.svg'),
          },
          {
            text: '중국어',
            value: 'zh',
            imgSrc: require('assets/image/img_flag_CN.svg'),
          },
        ],
        text: 'text',
        value: 'value',
        selectedOption: 'en',
      },
      switchOption: {
        value: true,
      },
      progress: {
        max: 100,
        value: 100,
      },
      search: {
        placeholder: '회원명 검색',
        value: '',
      },
      listviewSample: [
        {
          profile: require('assets/image/img-default-user.svg'),
          name: '버넥트 리모트 01',
          id: 'example@example.com',
          role: 'Master',
        },
        {
          profile: require('assets/image/img-default-user.svg'),
          name: '버넥트 리모트 01',
          id: 'example@example.com',
          role: 'Master',
        },
        {
          profile: require('assets/image/img-default-user.svg'),
          name: '버넥트 리모트 01',
          id: 'example@example.com',
          role: 'Master',
        },
      ],
      selectOptions: {
        options: [
          {
            value: '1',
            text: '테스트',
          },
          {
            value: '2',
            text: '테스트2',
          },
          {
            value: '3',
            text: '테스트3',
          },
        ],
        value: 'value',
        text: 'text',
        selection: '테스트',
      },
      toogleButton: {
        description: '마이크 on/off',
        size: 24,
      },
      toggle: false,
      popover: {
        position: 'bottom',
        positionOptions: [
          'top',
          'top-start',
          'top-end',
          'right',
          'right-start',
          'right-end',
          'bottom',
          'bottom-start',
          'bottom-end',
          'left',
          'left-start',
          'left-end',
        ],
        trigger: 'click',
        triggerOptions: ['click', 'hover', 'focus', 'manual'],
      },
      tooltip: {
        content: '마이크 on / off',
        placement: 'bottom',
        placementOptions: [
          'top',
          'top-start',
          'top-end',
          'right',
          'right-start',
          'right-end',
          'bottom',
          'bottom-start',
          'bottom-end',
          'left',
          'left-start',
          'left-end',
        ],
      },
      modal: {
        visible: false,
      },
    }
  },
  methods: {
    toogleOnOff() {},
    searchText(text) {
      this.search.value = text
    },
    selectOptionValue(option) {
      this.selectOptions.selection = option[this.selectOptions.text]
    },
  },
}
</script>

<style lang="scss" scoped>
.test {
  height: 100%;
  padding: 30px;
  // background-color: #bebebe;
}

.text-title {
  margin-bottom: 30px;
  font-weight: 500;
}

.test-section {
  margin-bottom: 30px;
  background-color: #787878;
  border-radius: 10px;

  &.select {
    // background-color: #1d1d1f;
  }

  .subtitle {
    color: #fff;
    font-weight: 500;
  }
  .action-box {
    display: flex;
    .component {
      position: relative;
      width: 100%;
    }

    .props {
      flex-shrink: 0;
      width: 600px;
      &-option {
        display: flex;
        margin-bottom: 10px;
      }
      &-title {
        flex-shrink: 0;
        width: 100px;
        padding-right: 10px;
        color: #fff;
        text-align: right;
      }
      &-options {
        width: 100%;
      }
    }
  }
}
.btn {
  margin-bottom: 10px;
}
</style>
