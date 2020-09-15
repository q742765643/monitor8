<template>
  <div id="infowindows">
    <div id="title" class="title">
      <span>设备属性信息</span>
      <span class="icon iconfont iconbaseline-close-px" v-on:click="closeWindow"></span>
    </div>
    <div class="info">
      <div class="column">
        <div class="cell">
          <span>
            设备别名:
            <i></i>
          </span>
          <span><input placeholder="请输入文本" type="text"/></span>
        </div>
        <div class="cell">
          <span>
            设备名称:
            <i></i>
          </span>
          <span>456</span>
        </div>
        <div class="cell">
          <span>
            设备IP:
            <i></i>
          </span>
          <span>123</span>
        </div>
        <div class="cell">
          <span>
            网关地址:
            <i></i>
          </span>
          <span>sdads</span>
        </div>
        <div class="cell">
          <span>
            设备类型:
            <i></i>
          </span>
          <span
            ><select>
              <option v-for="(item, index) in deviceType" :key="index" value="item">{{ item }} </option>
            </select></span
          >
        </div>
        <div class="cell">
          <span>
            监视方式:
            <i></i>
          </span>
          <span>
            <select>
              <option v-for="(item, index) in monitorType" :key="index" value="item">{{ item }} </option>
            </select></span
          >
        </div>
        <div class="cell">
          <span>
            所在区域:
            <i></i>
          </span>
          <span>
            <select>
              <option v-for="(item, index) in positionType" :key="index" value="item">{{ item }} </option>
            </select></span
          >
        </div>
        <div class="cell">
          <span>
            详细地址:
            <i></i>
          </span>
          <span><input placeholder="请输入文本" style="width:150%" type="text"/></span>
        </div>

        <div class="cell">
          <span>
            负责人:
            <i></i>
          </span>
          <span><input placeholder="请输入文本" type="text"/></span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import $ from 'jquery';
  export default {
    data() {
      return {
        deviceType: ['windows服务器', 'linux服务器', '打印机', '交换机', '监视器', '其它'],
        monitorType: ['snmp协议接口', '代理接口', 'ping'],
        positionType: ['机房区', '值班区', '办公区四楼', '办公区五楼', '其它'],
      };
    },
    mounted() {
      this.moveWindow();
    },
    methods: {
      moveWindow() {
        $('#title').mousedown(function(event) {
          var isMove = true;
          var abs_x = event.pageX - $('#infowindows').offset().left;
          var abs_y = event.pageY - $('#infowindows').offset().top;

          let parentX = $('#column1').offset().left;
          let parentY = $('#column1').offset().top;
          let p = $('#column1').offset();
          debugger;

          $(document)
            .mousemove(function(event) {
              if (isMove) {
                var obj = $('#infowindows');
                obj.css({
                  left: event.pageX - abs_x - parentX,
                  top: event.pageY - abs_y + parentY,
                });

                /*   let ax = abs_x;
                let ay = abs_y;
                let l = event.pageX - abs_x;
                let t = event.pageY - abs_y;
                debugger; */
              }
            })
            .mouseup(function() {
              isMove = false;
            });
        });
      },
      closeWindow() {
        this.$parent.closeWindow();
      },
    },
  };
</script>

<style lang="scss" scoped>
  #infowindows {
    input {
      padding-left: 0.0625rem;
      border: 1px solid #d2d2d2;
      background-color: transparent;
      height: 0.4rem;
      width: 2rem;
      border-radius: 0.0625rem;
    }
    select {
      border: 1px solid #d2d2d2;
      background-color: transparent;
      width: 2rem;
      height: 0.4rem;
      border-radius: 0.0625rem;
    }
    input:focus {
      outline: 1px solid #0f70e0;
      border-radius: 0.0625rem;
    }

    transform: translate(-50%, -50%);
    z-index: 501;
    position: absolute;
    left: 50%;
    top: 50%;
    box-shadow: 0 0 5px #666;
    background: white;
    width: 5rem;
    .title {
      font-family: Georgia;
      font-weight: 600;
      height: 0.625rem;
      padding-left: 0.15rem;
      font-size: 0.2rem;
      border-bottom: solid 0.025rem #eef5fd;
      .icon {
        position: relative;
        float: right;
        right: 0.25rem;
        cursor: pointer;
      }
      span {
        line-height: 0.625rem;
      }
    }

    .info {
      padding: 0.4rem;
      .column {
        height: 4.875rem;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        .cell {
          flex: 1;
          display: flex;
          span:first-child {
            font-size: 0.1875rem;
            width: 1rem;
            display: inline-block;
            font-weight: 600;
            text-align: justify;
            i {
              width: 100%;
              display: inline-block;
            }
          }

          span:last-child {
            margin-left: 0.125rem;
          }
        }
      }
    }
  }
</style>
