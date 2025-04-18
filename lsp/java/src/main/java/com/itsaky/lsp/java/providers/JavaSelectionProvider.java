/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.itsaky.lsp.java.providers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.itsaky.androidide.utils.Logger;
import com.itsaky.lsp.api.ISelectionProvider;
import com.itsaky.lsp.java.compiler.CompilerProvider;
import com.itsaky.lsp.java.visitors.FindBiggerRange;
import com.itsaky.lsp.models.ExpandSelectionParams;
import com.itsaky.lsp.models.Position;
import com.itsaky.lsp.models.Range;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

/**
 * Selection provider implementation for Java.
 * @author Akash Yadav
 */
public class JavaSelectionProvider implements ISelectionProvider {
    
    private final CompilerProvider compiler;
    
    private static final Logger LOG = Logger.instance ("JavaSelectionProvider");
    
    public JavaSelectionProvider (CompilerProvider compiler) {
        this.compiler = compiler;
    }
    
    @Override
    public Range expandSelection (@NonNull ExpandSelectionParams params) {
        return compiler.compile (params.getFile ()).getWithTask (task -> {
            final CompilationUnitTree root = task.root (params.getFile ());
            final FindBiggerRange rangeFinder = new FindBiggerRange (task.task, root);
            final Range range = rangeFinder.scan (root, params.getSelection ());
            
            if (range != null) {
                LOG.debug ("Expanding selection to range", range);
                return range;
            }
            
            LOG.debug ("Unable to expand selection");
            return params.getSelection ();
        });
    }
}
