/**
 * 1. 有个特别的现象：HasMany的实现既有DB也有Memory，但HasOne的实现没有DB的。可能因为这个案例里，没有需要分开查询的性能瓶颈吧
 * 2. 一个对象是DB实现，不代表他下面所有的association都要用DB的实现，选择实现应该是灵活的才对，
 * 应该由具体的业务association实现自己决定，而不是技术的EntityList、Reference决定。
 */
package reengineering.ddd.mybatis;

