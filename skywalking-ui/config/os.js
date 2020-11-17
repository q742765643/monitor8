/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

let fs = require('fs')
let path = require('path')

let src1 = '../node_modules/element-ui/types/message.d.ts'
annotation(src1, '$message: ElMessage')
let src2 = '../node_modules/element-ui/types/message-box.d.ts'
annotation(src2, '$confirm: ElMessageBoxShortcutMethod')
let src3 = '../node_modules/@antv/g6/lib/algorithm/structs/stack/index.d.ts'
annotation(src3, 'get length(): number;')

function annotation(src, params) {
  fs.readFile(path.resolve(__dirname, src), 'utf8', function(err, files) {
    if (!err && files !== '') {
      let val = params
      let has = `// ${params}`
      let start = files.indexOf(val)
      let start2 = files.indexOf(has)
      if (start > -1 && start2 === -1) {
        var result = files.replace(val, has)
        fs.writeFile(
            path.resolve(__dirname, src),
            result,
            'utf8',
            function(err) {
              if (err) {
                console.log(err)
              }
            }
        )

        console.log(params + ' 注释成功！')
      } else {
        console.log('没有需要注释对或者已经注释了！')
      }
    } else {
      console.log(
          params + ' 没有需要注释对或者已经注释了或者注释文件失败！'
      )
    }
  })
}
