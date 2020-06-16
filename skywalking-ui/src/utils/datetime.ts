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

import { Duration } from '@/types/global';

/**
 * init or generate durationRow Obj and save localStorage.
 */
const getDurationRow = (): Duration => {
  const durationRowString = localStorage.getItem('durationRow');
  let durationRow: Duration;
  if (durationRowString && durationRowString !== '') {
    durationRow = JSON.parse(durationRowString);
    durationRow = {
      start: new Date(durationRow.start),
      end: new Date(durationRow.end),
      step: durationRow.step,
    };
  } else {
    durationRow = {
      start: new Date(new Date().getTime() - 900000),
      end: new Date(),
      step: 'MINUTE',
    };
    localStorage.setItem('durationRow', JSON.stringify(durationRow, null, 0));
  }
  return durationRow;
};

export default getDurationRow;
