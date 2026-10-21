# Sudoku

In the function `generatePuzzleNumbers()`:

For `val solvedPuzzle` convert this rust function to kotlin to generate a completed puzzle:

```rust
fn get_complete_grid() -> [u8;81] {
    let mut rng = rand::rng();
    let mut nums: [u8; 81] = [0; 81];
    let mut i = 0 as usize;
    let mut num_tries = 0;
    loop {
        let r = i / 9;
        // let c = i % 9;
        let possible_options = get_possible_options(&nums, i);
        // println!("{}: {:?}",i,possible_options);
        // print_grid(nums);
        if possible_options.is_empty() {
            //clear entire row
            for x in (r*9)..(r*9+9) {
                nums[x] = 0
            }
            i = r*9;
            // println!("i reset {}", i)
        } else {
            nums[i] = *possible_options.choose(&mut rng).unwrap();
            i += 1;
        }
        if i == 200 {
            break;
        }
        if i>80 {
            break;
        }
        num_tries += 1;
        // println!("num_tries: {}", num_tries);
        if num_tries == 1000 {
            num_tries = 0;
            nums = [0; 81];
            i = 0;
        }
    }

    nums
}

fn get_possible_options(puzzle:&[u8;81],i:usize) -> Vec<u8> {
    if puzzle[i]!=0 {
        return vec![puzzle[i]];
    }
    let r = i / 9;
    let c = i % 9;
    // println!("i{}: R{},C{}",i,r,c);
    let mut possible_options = vec![1u8,2,3,4,5,6,7,8,9];
    for j in 0..9 {
        //do not match digits in row
        let ji = r * 9 + j;
        // println!("R: {} - {}", ji, puzzle[ji]);
        if puzzle[ji]!=0 {
            possible_options.retain(|d| *d!=puzzle[ji]);
        }
        //do not match digits in col
        let ji = c + 9 * j;
        // println!("C: {} - {}", ji, puzzle[ji]);
        if puzzle[ji]!=0 {
            possible_options.retain(|d| *d!=puzzle[ji]);
        }
        //do not match digits in grid
        let mut ji = 0;
        for x in 0..9 {
            if GRID[x].contains(&i) {
                ji = x;
                break;
            }
        }
        ji = GRID[ji][j] as usize;
        // println!("G: {} - {}", ji, puzzle[ji]);
        if puzzle[ji]!=0 {
            possible_options.retain(|d| *d!=puzzle[ji]);
        }

        //do not match possible matches that happen in a row
        

        // println!("{}: {:?}",i,possible_options);
    }

    possible_options
}

```

For `val unsolvedPuzzle` convert these rust functions to kotlin to generate a completed puzzle. It should take in the val solvedPuzzle from above as the completed grid used to generate the solvable puzzle.

```rust
fn generate_solvable_puzzle(puzzle_complete: [u8; 81]) -> [u8;81] {
    //generate a puzzle that can be solved with programmed algorithms
    let mut puzzle = puzzle_complete.clone();
    // print_grid(&puzzle);

    let mut rng = rand::rng();
    //remove random digit
    // let mut idxs_to_remove:Vec<u8> = Vec::new();
    let mut idxs_to_keep:Vec<u8> = (0..81).collect();

    // println!("{:?}", idxs_to_keep);
    let mut iattempt = 0;
    let mut removed_digit: u8;
    loop {
        let idx_selector = idxs_to_keep.clone();
        let keep_idx = idx_selector.choose(&mut rng).unwrap();
        // println!("{:?}", keep_idx);
        removed_digit = puzzle[*keep_idx as usize];
        puzzle[*keep_idx as usize] = 0;
        // println!("{:?}", idxs_to_keep);

        // println!("start solve");
        let solved = solve_puzzle(&puzzle, None);
        // println!("end solve");
        if !is_solved(&solved) {
            // print_grid(&puzzle);
            //can not solve this, so consider previous iteration as the final puzzle
            puzzle[*keep_idx as usize] = removed_digit;
            if iattempt > 20 {
                break;
            }
            iattempt += 1;
            // println!("iattempt: {iattempt}, remove index {keep_idx}");
        } else {
            idxs_to_keep.retain(|i| i!=keep_idx);
            // println!("idxs_to_keep: {:?}", idxs_to_keep);
        }

    }

    puzzle
}

fn solve_puzzle(puzzle:&[u8;81], presolved:Option<&[u8;81]>) -> [u8;81] {
    let mut possibles:[Vec::<u8>;81] = std::array::from_fn(|_| Vec::new());
    let mut solved = puzzle.clone();
    
    loop {
        //only available option
        let mut num_solved_by_only_option:u8 = 0;
        for i in 0..81 {
            let r = i / 9;
            let c = i % 9;
            // println!("i{}: R{},C{}: {}",i,r,c,solved[i]);
            if solved[i] == 0 {
                if possibles[i].len()==0 {
                    let possible_options = get_possible_options(&solved, i);
                    // println!("possible_options: {:?}", possible_options);
                    possibles[i] = possible_options;
                }
                if possibles[i].len()==1 {
                    //only one option
                    solved[i] = possibles[i][0];
                    num_solved_by_only_option +=1;
                    debug!("{},{} only possible option {}", r, c, solved[i]);
                    if let Some(presolved) = presolved {
                        if solved[i] != presolved[i] {
                            panic!("{}:{} solved to {}, but should be {}", r, c, solved[i], presolved[i]);
                        }
                    }
                } else {
                    let possible_options = get_possible_options(&solved, i);
                    // possibles[i] = possible_options;
                    if possible_options.len() < possibles[i].len() {
                        possibles[i] = possible_options;
                    }
                }
            } else {
                if possibles[i].len()!=1 {
                    possibles[i] = vec![solved[i]];
                }
            }
        }

        remove_possible_matches_from_lineups(&mut possibles);

        //in each 3x3 square, are there any single options
        let mut num_single_option_in_sq:u8 = 0;
        // print_possibles(&possibles);
        for sq in 0..9 {
            // println!("Square {}", sq);
            for i in GRID[sq] {
                let r = i / 9;
                let c = i % 9;
                let possible_options = &possibles[i];
                // println!("  {:?}",possible_options);
                if possible_options.len()>1 {
                    //do any options exist in any other blocks of this square?
                    for op in possible_options {
                        let mut found_op = false;
                        // println!("check {}: {}", i, op);
                        for ichk in GRID[sq] {
                            if ichk!=i {
                                let chk_possible_options = &possibles[ichk];
                                if chk_possible_options.contains(op) {
                                    // println!("  chk_possible_options {:?} contains op {}", chk_possible_options, op);
                                    found_op = true;
                                    break;
                                }
                            }
                        }
                        if !found_op {
                            // println!("resolved {}: {}", i, op);
                            //so this option must be the answer
                            solved[i] = *op;
                            possibles[i] = vec![solved[i]];
                            num_single_option_in_sq += 1;
                            debug!("{},{} single option in 3x3 {}", r, c, solved[i]);
                            if let Some(presolved) = presolved {
                                if solved[i] != presolved[i] {
                                    panic!("{}:{} solved to {}, but should be {}", r, c, solved[i], presolved[i]);
                                }
                            }
                            break;
                        }
                    }
                }
            }
            // break;
        }

        let mut num_single_option_in_row:u8 = 0;
        for r in 0..9 {
            for c in 0..9 {
                let i = r*9+c;
                let possible_options = &possibles[i];
                // println!("  {:?}",possible_options);
                if possible_options.len()>1 {
                    //do any options exist in any other blocks of this row?
                    for op in possible_options {
                        let mut found_op = false;
                        // println!("check {}: {}", i, op);
                        for cchk in 0..9 {
                            let ichk = r*9+cchk;
                            if ichk!=i {
                                let chk_possible_options = &possibles[ichk];
                                if chk_possible_options.contains(op) {
                                    // println!("  chk_possible_options {:?} contains op {}", chk_possible_options, op);
                                    found_op = true;
                                    break;
                                }
                            }
                        }
                        if !found_op {
                            // println!("resolved {}: {}", i, op);
                            //so this option must be the answer
                            solved[i] = *op;
                            possibles[i] = vec![solved[i]];
                            num_single_option_in_row += 1;
                            debug!("{},{} single option in row {}", r, c, solved[i]);
                            if let Some(presolved) = presolved {
                                if solved[i] != presolved[i] {
                                    panic!("{}:{} solved to {}, but should be {}", r, c, solved[i], presolved[i]);
                                }
                            }
                            break;
                        }
                    }
                }
            }
            // break;
        }

        let mut num_single_option_in_col:u8 = 0;
        for c in 0..9 {
            for r in 0..9 {
                let i = r*9+c;
                let possible_options = &possibles[i];
                // println!("  {:?}",possible_options);
                if possible_options.len()>1 {
                    //do any options exist in any other blocks of this row?
                    for op in possible_options {
                        let mut found_op = false;
                        // println!("check {}: {}", i, op);
                        for rchk in 0..9 {
                            let ichk = rchk*9+c;
                            if ichk!=i {
                                let chk_possible_options = &possibles[ichk];
                                if chk_possible_options.contains(op) {
                                    // println!("  {},{} chk_possible_options {:?} contains op {}", rchk, c, chk_possible_options, op);
                                    found_op = true;
                                    break;
                                } else {
                                    // println!("  {},{} chk_possible_options {:?} NOT contains op {}", rchk, c, chk_possible_options, op);
                                }
                            }
                        }
                        if !found_op {
                            // println!("resolved {}: {}", i, op);
                            //so this option must be the answer
                            solved[i] = *op;
                            possibles[i] = vec![solved[i]];
                            num_single_option_in_col += 1;
                            debug!("{},{} single option in col {}", r, c, solved[i]);
                            if let Some(presolved) = presolved {
                                if solved[i] != presolved[i] {
                                    panic!("{}:{} solved to {}, but should be {}", r, c, solved[i], presolved[i]);
                                }
                            }
                            break;
                        }
                    }
                }
            }
            // break;
        }

        //visible pairs in 3x3 squares
        let mut num_visible_pair_in_sq:u8 = 0;
        //if two possible numbers only exist in two positions soley, remove them from other positions within the 8 other positions in the set
        for sq in 0..9 {
            // println!("Square {}", sq);
            let mut found_matches:[u8;2] = [0;2];
            let mut found_in:[usize;2] = [0;2];
            for i in GRID[sq] {
                let r = i / 9;
                let c = i % 9;
                let possible_options = &possibles[i];
                // println!("{:?}",possible_options);
                //is this a pair and does it exist elsewhere in the 9?
                if possible_options.len()==2 {
                    for ichk in GRID[sq] {
                        if ichk!=i {
                            let chk_possible_options = &possibles[ichk];
                            if chk_possible_options == possible_options {
                                found_in[0] = i;
                                found_in[1] = ichk;
                                found_matches[0] = possible_options[0];
                                found_matches[1] = possible_options[1];
                                debug!("{} {},{} visible pairs 3x3 found_matches:{:?} found_in:{:?}", sq, r, c, found_matches, found_in);
                                break;
                            }
                        }
                    }
                }
                if found_matches[0]!=0 {
                    break;
                }
            }
            if found_matches[0]!=0 {
                //found, now remove these two numbers from all other possible options vecs
                for iremove in GRID[sq] {
                    if !found_in.contains(&iremove) {
                        if possibles[iremove].contains(&found_matches[0]) || possibles[iremove].contains(&found_matches[1]) {
                            num_visible_pair_in_sq += 1;
                            possibles[iremove].retain(|d| *d!=found_matches[0]);
                            possibles[iremove].retain(|d| *d!=found_matches[1]);
                        }
                    }
                }
            }
        }

        //visible pairs in rows
        let mut num_visible_pair_in_row:u8 = 0;
        //if two possible numbers only exist in two positions soley, remove them from other positions within the 8 other positions in the set
        for r in 0..9 {
            let mut found_matches:[u8;2] = [0;2];
            let mut found_in:[usize;2] = [0;2];
            for c in 0..9 {
                let i = r*9+c;
                let possible_options = &possibles[i];
                // println!("{:?}",possible_options);
                //is this a pair and does it exist elsewhere in the 9?
                if possible_options.len()==2 {
                    for cchk in 0..9 {
                        let ichk = r*9+cchk;
                        if ichk!=i {
                            let chk_possible_options = &possibles[ichk];
                            if chk_possible_options == possible_options {
                                found_in[0] = i;
                                found_in[1] = ichk;
                                found_matches[0] = possible_options[0];
                                found_matches[1] = possible_options[1];
                                debug!("{},{} visible pairs row found_matches:{:?} found_in:({} {:?}),({} {:?})", r, c, found_matches, found_in[0], possibles[i], found_in[1], possibles[ichk]);
                                break;
                            }
                        }
                    }
                }
                if found_matches[0]!=0 {
                    break;
                }
            }
            if found_matches[0]!=0 {
                //found, now remove these two numbers from all other possible options vecs
                for cremove in 0..9 {
                    let iremove = r*9+cremove;
                    // println!("iremove: {} {:?}", iremove, possibles[iremove]);
                    if !found_in.contains(&iremove) {
                        if possibles[iremove].contains(&found_matches[0]) || possibles[iremove].contains(&found_matches[1]) {
                            debug!("rows found or({:?}) in pos {}: {:?}", found_matches, iremove, possibles[iremove]);
                            num_visible_pair_in_row += 1;
                            possibles[iremove].retain(|d| *d!=found_matches[0]);
                            possibles[iremove].retain(|d| *d!=found_matches[1]);
                            // println!("new pos at {}: {:?}", iremove, possibles[iremove]);
                        }
                    }
                }
            }
        }
        // println!("{}", format_possibles(&possibles));

        //visible pairs in columns
        // print_possibles(&possibles);
        let mut num_visible_pair_in_col:u8 = 0;
        //if two possible numbers only exist in two positions soley, remove them from other positions within the 8 other positions in the set
        for c in 0..9 {
            let mut found_matches:[u8;2] = [0;2];
            let mut found_in:[usize;2] = [0;2];
            for r in 0..9 {
                let i = r*9+c;
                let possible_options = &possibles[i];
                // println!("{:?}",possible_options);
                //is this a pair and does it exist elsewhere in the 9?
                if possible_options.len()==2 {
                    for rchk in 0..9 {
                        let ichk = rchk*9+c;
                        if ichk!=i {
                            let chk_possible_options = &possibles[ichk];
                            if chk_possible_options == possible_options {
                                found_in[0] = i;
                                found_in[1] = ichk;
                                found_matches[0] = possible_options[0];
                                found_matches[1] = possible_options[1];
                                debug!("{},{} visible pairs col found_matches:{:?} found_in:({} {:?}),({} {:?})", r, c, found_matches, found_in[0], possibles[i], found_in[1], possibles[ichk]);
                                break;
                            }
                        }
                    }
                }
                if found_matches[0]!=0 {
                    break;
                }
            }
            if found_matches[0]!=0 {
                //found, now remove these two numbers from all other possible options vecs
                for rremove in 0..9 {
                    let iremove = rremove*9+c;
                    if !found_in.contains(&iremove) {
                        if possibles[iremove].contains(&found_matches[0]) || possibles[iremove].contains(&found_matches[1]) {
                            debug!("cols found or({:?}) in pos {}: {:?}", found_matches, iremove, possibles[iremove]);
                            num_visible_pair_in_col += 1;
                            possibles[iremove].retain(|d| *d!=found_matches[0]);
                            possibles[iremove].retain(|d| *d!=found_matches[1]);
                            // println!("new pos at {}: {:?}", iremove, possibles[iremove]);
                        }
                    }
                }
            }
        }
        // print_possibles(&possibles);

        // let mut num_eliminate_by_double_rows:u8 = 0;

        // //hidden pairs in 3x3 squares
        // let mut num_hidden_pair_in_sq:u8 = 0;
        // //logic 2: if two numbers only exist in two squares and have other numbers, remove paired numbers from other squares

        // //hidden pairs in rows
        // let mut num_hidden_pair_in_row:u8 = 0;

        debug!(r"
num_solved_by_only_option: {}
num_single_option_in_sq: {}
num_single_option_in_row: {}
num_single_option_in_col: {}
num_visible_pair_in_sq: {}
num_visible_pair_in_row: {}
num_visible_pair_in_col: {}
",
num_solved_by_only_option,
num_single_option_in_sq,
num_single_option_in_row,
num_single_option_in_col,
num_visible_pair_in_sq,
num_visible_pair_in_row,
num_visible_pair_in_col,
);
        if num_solved_by_only_option==0
            && num_single_option_in_sq==0
            && num_single_option_in_row==0
            && num_single_option_in_col==0
            && num_visible_pair_in_sq==0
            && num_visible_pair_in_row==0
            && num_visible_pair_in_col==0
        {
            break;
        }
        // print_grid(&puzzle);
    }
    debug!("\n{}", format_possibles(&possibles));
    solved
}

fn is_solved(puzzle:&[u8;81]) -> bool {
    !puzzle.iter().any(|f| *f==0)
}

```
