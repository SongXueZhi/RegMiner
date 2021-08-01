/*
 *
 *  * Copyright 2021 SongXueZhi
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package regminer.coverage.model;
// tuple <package,class,method>
public class CoverNode {
 CoverMethod coverMethod;
 CoverPackage coverPackage;
 CoverClass coverClass;

 public CoverNode(CoverPackage coverPackage,CoverClass coverClass,CoverMethod coverMethod){
  this.coverPackage=coverPackage;
  this.coverClass=coverClass;
  this.coverMethod=coverMethod;
 }
 public CoverMethod getCoverMethod() {
  return coverMethod;
 }

 public void setCoverMethod(CoverMethod coverMethod) {
  this.coverMethod = coverMethod;
 }

 public CoverPackage getCoverPackage() {
  return coverPackage;
 }

 public void setCoverPackage(CoverPackage coverPackage) {
  this.coverPackage = coverPackage;
 }

 public CoverClass getCoverClass() {
  return coverClass;
 }

 public void setCoverClass(CoverClass coverClass) {
  this.coverClass = coverClass;
 }
}
