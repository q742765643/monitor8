<template>
  <div id="viewMonitor">
    <div><planeTitle titleName="监控总览"></planeTitle></div>
    <div id="contnet">
      <div class="ringchart">
        <div id="main0"></div>
        <div class="name">链路设备</div>
      </div>
      <div class="ringchart">
        <div id="main1"></div>
        <div class="name">主机设备</div>
      </div>
      <div class="ringchart">
        <div id="main2"></div>
        <div class="name">数据任务</div>
      </div>
      <div class="ringchart">
        <div id="main3"></div>
        <div class="name">进程任务</div>
      </div>
    </div>
  </div>
</template>

<script>
  import planeTitle from '@/components/titile/planeTitle.vue';
  import { remFontSize } from '@/components/utils/fontSize.js';
  import echarts from 'echarts';
  // 接口地址
  import hongtuConfig from '@/utils/services';
  export default {
    data() {
      return {
        ringChart0: '',
        ringChart1: '',
        ringChart2: '',
        ringChart3: '',

        totalData: 45,
        ringList: [
          {
            name: '链路设备',
            color1: '#C818F2',
            color2: '#0E6EBA',
            id: 'main0',
          },
          {
            name: '主机设备',
            color1: '#FFB20E',
            color2: '#FF7559',
            id: 'main1',
          },
          {
            name: '数据任务',
            color1: '#30D03C',
            color2: '#25BF0F',
            id: 'main2',
          },
          {
            name: '进程任务',
            color1: '#FA905A',
            color2: '#6F82FB',
            id: 'main3',
          },
        ],
      };
    },
    name: 'viewMonitor',
    components: { planeTitle },
    created() {},

    async mounted() {
      //监控总览
      await hongtuConfig.getMonitorViewVo().then((res) => {
        if (res.code == 200) {
          let dataarray = res.data;
          dataarray.forEach((element, index) => {
            if (element.classify == this.ringList[index].name) {
              this.ringList[index].value = element.num;
            }
          });
        } else {
          alert('监控总览数据请求失败2');
        }
      });
      this.$nextTick(() => {
        this.drawPipe('main0', this.ringList[0]);
        this.drawPipe('main1', this.ringList[1]);
        this.drawPipe('main2', this.ringList[2]);
        this.drawPipe('main3', this.ringList[3]);
      });
      window.addEventListener('resize', () => {
        setTimeout(() => {
          this.ringChart0.resize();
          this.ringChart1.resize();
          this.ringChart2.resize();
          this.ringChart3.resize();
        }, 500);
      });
    },
    methods: {
      drawPipe(id, data) {
        var datas = {
          ringColor: [
            {
              offset: 0,
              color: data.color1,
            },
            {
              offset: 1,
              color: data.color2,
            },
          ],
        };
        let option = {
          // backgroundColor: "#000",
          textStyle: {
            fontFamily: 'Alibaba-PuHuiTi-Regular',
          },

          title: {
            text: data.value,
            x: 'center',
            y: 'center',
            textStyle: {
              fontWeight: 'normal',
              color: data.color1,
              fontSize: remFontSize(0.4),
            },
          },
          color: ['#D6D6D4'],

          series: [
            {
              type: 'pie',
              clockWise: true,
              radius: ['73%', '85%'],
              itemStyle: {
                normal: {
                  label: {
                    show: false,
                  },
                  labelLine: {
                    show: false,
                  },
                },
              },
              hoverAnimation: false,
              data: [
                {
                  value: data.value,
                  name: '',
                  itemStyle: {
                    normal: {
                      color: {
                        colorStops: datas.ringColor,
                      },
                      label: {
                        show: false,
                      },
                      labelLine: {
                        show: false,
                      },
                    },
                  },
                },
                {
                  name: '',
                  value: this.totalData - data.value,
                },
              ],
            },
          ],
        };

        if (id == 'main0') {
          this.ringChart0 = echarts.init(document.getElementById(id));
          this.ringChart0.setOption(option);
        } else if (id == 'main1') {
          this.ringChart1 = echarts.init(document.getElementById(id));
          this.ringChart1.setOption(option);
        } else if (id == 'main2') {
          this.ringChart2 = echarts.init(document.getElementById(id));
          this.ringChart2.setOption(option);
        } else if (id == 'main3') {
          this.ringChart3 = echarts.init(document.getElementById(id));
          this.ringChart3.setOption(option);
        }
      },
    },
  };
</script>

<style lang="scss" scoped>
  #viewMonitor {
    height: 2.6875rem;
    width: 100%;
    box-shadow: $plane_shadow;
    #contnet {
      height: calc(2.6875rem - 0.75rem);
      background: $plane_border_color;
      display: flex;
      .ringchart {
        flex: 1;
        height: calc(2.6875rem - 0.75rem);
        background: $plane_border_color;
        width: 100%;
        background: #fff;
        margin: 0 2px;
        #main0,
        #main1,
        #main2,
        #main3 {
          height: calc(2.6875rem - 0.75rem - 0.5rem);
          width: 100%;
        }
        div:last-child {
          margin: 0 0 0 2px !important;
        }
        div:first-child {
          margin: 0 2px 0 0 !important;
        }
        .name {
          position: relative;
          bottom: 0;
          text-align: center;
          line-height: 0.5rem;
          font-size: 0.225rem;
        }
      }
    }
  }
</style>
