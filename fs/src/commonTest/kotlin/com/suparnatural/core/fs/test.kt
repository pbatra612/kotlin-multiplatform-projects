package com.suparnatural.core.fs

import kotlin.test.*

var testPath = FileSystem.contentsDirectory
const val testFile = "test.txt"
var testFilePath = testPath.absolutePath?.byAppending(testFile)!!
const val testBase64String = "SGVsbG8="
const val testString = "Hello"

const val testString2 = "World"

const val testJoinedBase64String = "SGVsbG9Xb3JsZA=="


class FileSystemTests {
    @BeforeTest
    fun prepare() {
        FileSystem.unlink(FileSystem.contentsDirectory.absolutePath!!)
        testPath = FileSystem.contentsDirectory
        testFilePath = testPath.absolutePath?.byAppending(testFile)!!
        assertTrue(FileSystem.touch(testFilePath))
        assertTrue(FileSystem.writeFile(testFilePath, testString, true))
    }


    @Test
    fun testKnownPaths() {
        assertNotNull(FileSystem.contentsDirectory)
        val d = FileSystem.contentsDirectory
        val c = FileSystem.cachesDirectory
        val t = FileSystem.temporaryDirectory

        val paths = listOf(d, c, t)
        paths.forEach {
            assertNotNull(it.absolutePath)
            assertNotNull(it.relativePath)
            assertTrue(it.absolutePath!!.component!!.isNotEmpty())
            assertTrue(it.relativePath!!.component!!.isNotEmpty())
        }



    }

    @Test
    fun testReadDir() {
        val path = FileSystem.contentsDirectory
        val stats = FileSystem.readDir(path.absolutePath!!)
        assertNotNull(stats)
        assertEquals(1, stats.size)
        assertEquals(testFile, stats[0].name)
    }

    @Test
    fun testStat() {
        val path = FileSystem.contentsDirectory
        val stat = FileSystem.stat(testFilePath)
        assertNotNull(stat)
        assertNotNull(FileSystem.stat(path.absolutePath?.component!!))
        assertEquals(testFile, stat.name)
    }

    @Test
    fun testReadFileUtf8() {
        assertTrue(FileSystem.writeFile(testFilePath, testString, false, ContentEncoding.Utf8))
        assertEquals(testString, FileSystem.readFile(testFilePath, ContentEncoding.Utf8))

    }

    @Test
    fun testReadFileAscii() {
        assertTrue(FileSystem.writeFile(testFilePath, testString, false, ContentEncoding.Ascii))
        assertEquals(testString, FileSystem.readFile(testFilePath, ContentEncoding.Ascii))
    }

    @Test
    fun testReadFileBase64ToBase64() {
        assertTrue(FileSystem.writeFile(testFilePath, testString, false, ContentEncoding.Base64))
        assertEquals(testString, FileSystem.readFile(testFilePath, ContentEncoding.Base64))
    }

    @Test
    fun testReadFileBase64ToUtf8() {
        assertTrue(FileSystem.writeFile(testFilePath, testString, false, ContentEncoding.Base64))
        assertEquals(testBase64String, FileSystem.readFile(testFilePath, ContentEncoding.Utf8))
    }


    @Test
    fun testWriteFile() {

        assertTrue(FileSystem.writeFile(testFilePath, testString, false, ContentEncoding.Utf8))
        assertEquals(testString, FileSystem.readFile(testFilePath, ContentEncoding.Utf8))

        assertTrue(FileSystem.writeFile(testFilePath, testString, false, ContentEncoding.Ascii))
        assertEquals(testString, FileSystem.readFile(testFilePath, ContentEncoding.Ascii))

        assertTrue(FileSystem.writeFile(testFilePath, testString, false, ContentEncoding.Base64))
        assertEquals(testString, FileSystem.readFile(testFilePath, ContentEncoding.Base64))

        assertTrue(FileSystem.writeFile(testFilePath, testString, false, ContentEncoding.Base64))
        assertEquals(testBase64String, FileSystem.readFile(testFilePath, ContentEncoding.Utf8))

    }

    @Test
    fun testAppendFileUtf8() {
        assertTrue(FileSystem.appendFile(testFilePath, testString2, false, ContentEncoding.Utf8))
        assertEquals("$testString$testString2", FileSystem.readFile(testFilePath, ContentEncoding.Utf8))
    }

    @Test
    fun testAppendFileAscii() {
        assertTrue(FileSystem.writeFile(testFilePath, "", false, ContentEncoding.Utf8))

        assertTrue(FileSystem.appendFile(testFilePath, testString, false, ContentEncoding.Ascii))
        assertTrue(FileSystem.appendFile(testFilePath, testString2, false, ContentEncoding.Ascii))
        assertEquals("$testString$testString2", FileSystem.readFile(testFilePath, ContentEncoding.Ascii))
    }

    @Test
    fun testAppendFileBase64toBase64() {
        assertTrue(FileSystem.writeFile(testFilePath, "", false, ContentEncoding.Utf8))
        assertTrue(FileSystem.appendFile(testFilePath, testString, false, ContentEncoding.Base64))
        assertTrue(FileSystem.appendFile(testFilePath, testString2, false, ContentEncoding.Base64))
        assertEquals("$testString$testString2", FileSystem.readFile(testFilePath, ContentEncoding.Base64))
    }

    @Test
    fun testAppendFileBase64toUtf8() {
        assertTrue(FileSystem.writeFile(testFilePath, "", false, ContentEncoding.Utf8))
        assertTrue(FileSystem.appendFile(testFilePath, testString, false, ContentEncoding.Base64))
        assertTrue(FileSystem.appendFile(testFilePath, testString2, false, ContentEncoding.Base64))
        assertEquals("$testJoinedBase64String", FileSystem.readFile(testFilePath, ContentEncoding.Utf8))
    }


    @Test
    fun testMkDir() {
        val paths = listOf(Triple("dir", second = false, third = true), Triple("dir1/dir2", second = true, third = true), Triple("dir4/dir5", second = false, third = false))
        paths.forEach {
            val path = testPath.absolutePath?.byAppending(it.first)!!
            assertEquals(it.third, FileSystem.mkdir(path, it.second))
            assertEquals(it.third, FileSystem.exists(path))
        }
    }


    @Test
    fun testExists() {
        assertTrue(FileSystem.exists(testFilePath))
    }

    @Test
    fun testUnlink() {
        assertTrue(FileSystem.unlink(testFilePath))
        assertFalse(FileSystem.exists(testFilePath))
    }

    @Test
    fun testMove1() {
        val newPath = testPath.absolutePath!!.byAppending("file2.txt")!!
        assertTrue(FileSystem.moveFile(testFilePath, newPath))
        assertTrue(FileSystem.exists(newPath))
        assertFalse(FileSystem.exists(testFilePath))
    }

    @Test
    fun testMove2() {
        assertFalse(FileSystem.moveFile(testFilePath, testPath.absolutePath?.byAppending("dir/file2.txt")!!))
    }

    @Test
    fun testCopy() {
        val newPath = testPath.absolutePath!!.byAppending("file2.txt")!!
        assertTrue(FileSystem.copyFile(testFilePath, newPath))
        assertTrue(FileSystem.exists(newPath))
        assertTrue(FileSystem.exists(testFilePath))
    }
}
