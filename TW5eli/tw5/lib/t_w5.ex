defmodule TW5 do
  @moduledoc """
  Documentation for `TW5`.
  """

  @doc """
  Hello world.
  """
  def main() do
    [alphabet, word , actions] = parseInput "actions.txt"
    dSet = findDependants(actions, []) |> MapSet.new()
    aSquared = squareSet(alphabet, []) |> MapSet.new()
    indSet = MapSet.difference(aSquared, dSet)
    indSet
  end

  def parseInput(path) do
    {:ok, contents} = File.read(path)
    [alph_str, word_str | actions_str] = String.split(contents, "\r\n")

    alphabet = alph_str   |> parseAlphabet
    word =    word_str    |> String.split("", trim: true)
    actions = actions_str |> Enum.map(&parseAction/1)

    [alphabet, word , actions]
  end

  @doc """
  """
  def parseAlphabet(line) do
    letters = line
              |> String.split("")
              |> Enum.filter(fn(x) ->
      String.match?(x, ~r/^[[:alnum:]]+$/)
    end)
  end

  def parseAction(line) do
    [name, modified, "=" | tail] = String.split(line, " ")
    refs = MapSet.new(
      tail |> Enum.filter(fn(x) -> String.match?(x, ~r/^[[:alpha:]]+$/) end) # filter non-letters
            )
    [name, modified, refs]
  end


  @doc """
  """
  def dependent([_name, modified, refs], [_name2, modified2, refs2]) do
    MapSet.member?(refs, modified2) or MapSet.member?(refs2, modified)
    or modified == modified2
  end

  def findDependants([], acc) do acc end
  def findDependants([action | actions], acc) do
    [name, _mod, _refs] = action
    new_deps = [action | actions]
               |> Enum.filter(fn(a) -> dependent(a, action) end )
               |> Enum.map(fn([tmpName, _mod, _refs]) -> MapSet.new([name, tmpName]) end)
    findDependants(actions, acc ++ new_deps)
  end

  def squareSet([], acc) do acc end
  def squareSet([el | tail], acc) do
    pairs = [el | tail] |> Enum.map(fn(e) ->  MapSet.new([el, e]) end )
    squareSet(tail, acc ++ pairs)
  end

end
