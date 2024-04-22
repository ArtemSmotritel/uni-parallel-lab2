with Ada.Text_IO; use Ada.Text_IO;

procedure Lab2 is
   dim : constant integer := 100000;
   thread_num : constant integer := 3;

   arr : array(1..dim) of integer;

   type Num_And_Index is record
      Number : Integer;
      Index : Integer;
   end record;

   function part_min(start_index, end_index : in integer) return Num_And_Index is
      min : Integer := Standard'Max_Integer_Size;
      index : Integer := -1;
      result : Num_And_Index;
   begin
      for i in start_index..end_index loop
         if min > arr(i) then
            min := arr(i);
            index := i;
         end if;
      end loop;
      result.Number := min;
      result.Index := index;
      return result;
   end part_min;

   procedure Init_Arr is
   begin
      for i in 1..dim loop
         arr(i) := i;
      end loop;

      arr(dim / 2) := -100;
   end Init_Arr;

   protected part_manager is
      procedure set_part_min(local_min_and_index : in Num_And_Index);
      entry get_min_and_index(min_and_index : out Num_And_Index);
   private
      tasks_count : Integer := 0;
      min_and_index : Num_And_Index := (Standard'Max_Integer_Size, -1);
   end part_manager;

   protected body part_manager is
      procedure set_part_min(local_min_and_index : in Num_And_Index) is
      begin
         if min_and_index.Number > local_min_and_index.Number then
            min_and_index.Number := local_min_and_index.Number;
            min_and_index.Index := local_min_and_index.Index;
         end if;
         tasks_count := tasks_count + 1;
      end set_part_min;

      entry get_min_and_index(min_and_index : out Num_And_Index) when tasks_count = thread_num is
      begin
         min_and_index := part_manager.min_and_index;
      end get_min_and_index;
   end part_manager;

   task type Minimal_Worker is
      entry start(start_index, end_index : in Integer);
   end Minimal_Worker;

   task body Minimal_Worker is
      local_min_and_index : Num_And_Index;
      start_index, end_index : Integer;
   begin
      accept start(start_index, end_index : in Integer) do
         Minimal_Worker.start_index := start_index;
         Minimal_Worker.end_index := end_index;
      end start;
      local_min_and_index := part_min(start_index, end_index);
      part_manager.set_part_min(local_min_and_index);
   end Minimal_Worker;

   function parallel_part_min return Num_And_Index is
      result : Num_And_Index;
      batch_size, start_index, end_index : Integer;
      minimal_workers : array(1..thread_num) of Minimal_Worker;
   begin
      batch_size := dim / thread_num;
      for i in 1..thread_num loop
         start_index := (i - 1) * batch_size + 1;
         end_index := (if i = thread_num then dim else i * batch_size);
         minimal_workers(i).start(start_index => start_index,
                                  end_index   => end_index);
      end loop;

      part_manager.get_min_and_index(result);
      return result;
   end parallel_part_min;

   single_threaded_result: Num_And_Index;
   multi_threaded_result: Num_And_Index;

begin
   Init_Arr;
   single_threaded_result := part_min(1, dim);
   multi_threaded_result := parallel_part_min;

   Put_Line("Single threaded result:");
   Put_Line(single_threaded_result.Number'Img);
   Put_Line(single_threaded_result.Index'Img);

   Put_Line("Multi threaded result:");
   Put_Line(multi_threaded_result.Number'Img);
   Put_Line(multi_threaded_result.Index'Img);
end Lab2;
