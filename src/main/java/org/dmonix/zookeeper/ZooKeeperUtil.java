/**
 *  Copyright 2016 Peter Nerg
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.dmonix.zookeeper;

import static javascalautils.TryCompanion.Try;
import static org.apache.zookeeper.CreateMode.PERSISTENT;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.ZooKeeper;

import javascalautils.Try;
import javascalautils.Unit;

/**
 * Utility classes for ZooKeeper data management.
 * 
 * @author Peter Nerg
 * @since 1.0
 */
final class ZooKeeperUtil {

	/**
	 * Deletes a single path
	 * 
	 * @param zooKeeper
	 *            The ZooKeeper connection
	 * @param path
	 *            The path to delete
	 * @return The result
	 * @since 1.0
	 */
	static Try<Unit> deleteRecursive(ZooKeeper zooKeeper, String path) {
		// return Try(() -> {
		// List<String> children = children(zooKeeper, path).getOrElse(Collections::emptyList);
		// children.add(path);
		// return children.stream();
		// }).flatMap(stream -> {
		// return stream.map(child -> delete(zooKeeper, child)).reduce(Try.apply(Unit.Instance), (t1, t2) -> t1.flatMap(v -> t2));
		// });

		return Try(() -> {
			List<String> children = children(zooKeeper, path).getOrElse(Collections::emptyList);
			children.add(path);
			return children.stream();
		}).flatMap(stream -> stream.map(child -> delete(zooKeeper, child)).reduce(Try.apply(Unit.Instance), (t1, t2) -> t1.flatMap(v -> t2)));

		//
		// Try(() -> {
		// List<String> children = children(zooKeeper, path).getOrElse(Collections::emptyList);
		// children.add(path);
		//
		// children.stream().map(child -> delete(zooKeeper, child)).reduce(Try.apply(Unit.Instance), (t1,t2) -> t1.flatMap(v -> t2));
		//
		//
		// children(zooKeeper, path).flatMap(children -> {
		// return Try(() -> {
		// // attempt to delete all children recursively
		// for (String child : children) {
		// zk.delete(path + "/" + child, -1); // -1 for any version
		// }
		// zk.delete(path, -1); // -1 for any version
		// });
		// });
		// });
		//
		//
		// return Try(() -> {
		// // List<String> children = children(zk, path).getOrElse(Collections::emptyList);
		// children(zooKeeper, path).flatMap(children -> {
		// return Try(() -> {
		// // attempt to delete all children recursively
		// for (String child : children) {
		// String joinedZNode = path.equals("/") ? path + child : path + "/" + child;
		// deleteRecursive(zooKeeper, joinedZNode);
		// }
		// zooKeeper.delete(path, -1); // -1 for any version
		// });
		// });
		// });
	}

	/**
	 * Attempts to delete the provided path. <br>
	 * Will fail if ZK is down.
	 * 
	 * @param zooKeeper
	 *            The ZooKeeper connection
	 * @param path
	 *            The path
	 * @return The result of the operation
	 * @since 1.0
	 */
	static Try<Unit> delete(ZooKeeper zooKeeper, String path) {
		return Try(() -> {
			try {
				zooKeeper.delete(path, -1); // -1 for ANY version
			} catch (NoNodeException ex) {
				// ignored, deleting a non-existing node...is not a problem
			}
		});
	}

	static Try<List<String>> children(ZooKeeper zooKeeper, String path) {
		return Try(() -> zooKeeper.getChildren(path, null));
	}

	static boolean createRecursive(ZooKeeper zooKeeper, String path, byte[] data) throws KeeperException, InterruptedException {
		try {
			return createIfNotExist(zooKeeper, path, data);
		} catch (KeeperException.NoNodeException ex) {
			int pos = path.lastIndexOf("/");
			String parentPath = path.substring(0, pos);
			createRecursive(zooKeeper, parentPath, new byte[0]);
			return createIfNotExist(zooKeeper, path, data);
		}
	}

	static boolean createIfNotExist(ZooKeeper zooKeeper, String path, byte[] data) throws KeeperException, InterruptedException {
		boolean result = false;
		try {
			// check if the path exists before trying to create
			if (!exists(zooKeeper, path)) {
				zooKeeper.create(path, data, OPEN_ACL_UNSAFE, PERSISTENT);
				result = true;
			}
		}
		// this may still happen in a concurrent world some other process/thread may end up creating the path
		// after we did the exists(...) operation, so just in case we need to manage the exception
		catch (KeeperException.NodeExistsException ex) {
		}

		return result;
	}

	static boolean exists(ZooKeeper zooKeeper, String path) throws KeeperException, InterruptedException {
		return zooKeeper.exists(path, null) != null;
	}
}
