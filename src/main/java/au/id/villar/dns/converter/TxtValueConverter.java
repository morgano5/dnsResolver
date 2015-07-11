/*
 * Copyright 2015 Rafael Villar Villar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.id.villar.dns.converter;

import au.id.villar.dns.engine.ParseResult;
import au.id.villar.dns.engine.RRValueConverter;
import au.id.villar.dns.engine.Utils;

import java.util.Map;

public class TxtValueConverter implements RRValueConverter {

	@Override
	public Object convertToRawData(Object data) {
		if(!(data instanceof String))
			throw new IllegalArgumentException("Only String type supported");
		return data.toString();
	}

	@Override
	public Object getData(byte[] data, int offset, int length, Map<Integer, String> previousNames) {
		StringBuilder strData = new StringBuilder(data.length - 1);
		int index = offset;
		int total = offset + length;
		while(index < total) {
			ParseResult<String> result = Utils.getText(data, index);
			strData.append(result.value);
			index += result.bytesUsed;
			if(index < total) strData.append(' ');
		}
		return strData.toString();
	}

	@Override
	public <T> T convertValue(Object value, Class<T> tClass) {
		if(tClass != String.class && tClass != Object.class)
			throw new IllegalArgumentException("Only String type supported");
		return tClass.cast(value);
	}

	@Override
	public int writeRawData(Object value, byte[] array, int offset, int linkOffset, Map<String, Integer> nameLinks) {
		return Utils.writeText(value.toString(), array, offset);
	}

}
